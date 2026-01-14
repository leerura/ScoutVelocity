import pandas as pd
import numpy as np
from datetime import datetime, timedelta

# === 설정 ===
PLAYERS_FILE = 'players_final_100k.csv'
OUTPUT_FILE = 'match_records_3m_uniform.csv'
MATCHES_PER_PLAYER = 30  # 선수당 경기 수
TOTAL_EXPECTED = 100000 * MATCHES_PER_PLAYER # 300만 건

def get_position_category(pos_str):
    """ 포지션 문자열을 4개 그룹(FW, MF, DF, GK)으로 단순화 """
    if pd.isna(pos_str): return 'MF'
    pos_str = str(pos_str).upper()
    
    if 'GK' in pos_str: return 'GK'
    if any(x in pos_str for x in ['ST', 'CF', 'LW', 'RW', 'LF', 'RF']): return 'FW'
    if any(x in pos_str for x in ['CB', 'LB', 'RB', 'LWB', 'RWB']): return 'DF'
    return 'MF'

def generate_exact_records():
    print("1. 선수 데이터 로딩 중...")
    try:
        # 필요한 컬럼만 로드
        players = pd.read_csv(PLAYERS_FILE, usecols=['player_id', 'player_positions'], low_memory=False)
        current_players = len(players)
        print(f"   - 선수 수: {current_players}명")
    except FileNotFoundError:
        print("❌ 선수 파일이 없습니다.")
        return

    print(f"2. 데이터 증식 (선수당 {MATCHES_PER_PLAYER}경기)...")
    
    # [핵심 변경] 랜덤 추출이 아니라, 인덱스를 30번 반복해서 늘림
    # 예: [0, 1, 2] -> [0, 0, 0, ... , 1, 1, 1, ... , 2, 2, 2 ...]
    # 이렇게 하면 모든 선수가 정확히 30줄씩 생김
    expanded_df = players.loc[players.index.repeat(MATCHES_PER_PLAYER)].reset_index(drop=True)
    
    total_rows = len(expanded_df)
    print(f"   - 생성된 총 행 개수: {total_rows}건 (목표: {TOTAL_EXPECTED})")

    # 포지션 분석 (벡터 연산 전처리)
    expanded_df['role'] = expanded_df['player_positions'].apply(get_position_category)

    # 기본 프레임 설정
    expanded_df['record_id'] = range(1, total_rows + 1)
    
    # 날짜 생성 (2023-08-01 ~ 2024-05-30 시즌 내 랜덤 분산)
    # 선수별로 날짜가 겹칠 수 있지만, 각 행마다 랜덤이므로 30경기의 날짜는 서로 다를 확률이 높음
    print("3. 날짜 및 스탯 생성 중...")
    start_date = datetime(2023, 8, 1)
    random_days = np.random.randint(0, 300, total_rows)
    expanded_df['match_date'] = [start_date + timedelta(days=int(d)) for d in random_days]

    # --- 스탯 생성 (이전 로직과 동일 - 포지션 기반 가중치) ---
    goals = np.zeros(total_rows, dtype=int)
    assists = np.zeros(total_rows, dtype=int)
    pass_try = np.random.randint(10, 80, total_rows)
    
    # 마스크 생성
    is_fw = (expanded_df['role'] == 'FW').values
    is_mf = (expanded_df['role'] == 'MF').values
    is_df = (expanded_df['role'] == 'DF').values
    is_gk = (expanded_df['role'] == 'GK').values

    # 1) 공격수
    n_fw = is_fw.sum()
    if n_fw > 0:
        goals[is_fw] = np.random.choice([0, 1, 2, 3, 4], size=n_fw, p=[0.5, 0.3, 0.15, 0.04, 0.01])
        assists[is_fw] = np.random.choice([0, 1, 2], size=n_fw, p=[0.7, 0.2, 0.1])
        pass_try[is_fw] = np.random.randint(15, 50, size=n_fw)

    # 2) 미드필더
    n_mf = is_mf.sum()
    if n_mf > 0:
        goals[is_mf] = np.random.choice([0, 1, 2], size=n_mf, p=[0.8, 0.18, 0.02])
        assists[is_mf] = np.random.choice([0, 1, 2, 3], size=n_mf, p=[0.6, 0.3, 0.08, 0.02])
        pass_try[is_mf] = np.random.randint(40, 100, size=n_mf)

    # 3) 수비수
    n_df = is_df.sum()
    if n_df > 0:
        goals[is_df] = np.random.choice([0, 1], size=n_df, p=[0.95, 0.05])
        pass_try[is_df] = np.random.randint(30, 80, size=n_df)

    # 4) 골키퍼 (골/어시 0 고정)
    n_gk = is_gk.sum()
    if n_gk > 0:
        goals[is_gk] = 0
        assists[is_gk] = 0
        pass_try[is_gk] = np.random.randint(5, 30, size=n_gk)

    # 후처리 (패스 성공, 평점)
    success_rate = np.random.uniform(0.70, 0.95, total_rows)
    pass_success = (pass_try * success_rate).astype(int)
    
    ratings = np.random.normal(6.5, 1.0, total_rows)
    ratings += (goals * 1.0) + (assists * 0.5)
    ratings = np.clip(ratings, 4.0, 10.0).round(1)

    # 데이터 할당
    expanded_df['goals'] = goals
    expanded_df['assists'] = assists
    expanded_df['pass_try'] = pass_try
    expanded_df['pass_success'] = pass_success
    expanded_df['rating'] = ratings

    # 정리 (불필요 컬럼 제거)
    final_df = expanded_df.drop(columns=['role', 'player_positions'])

    # 4. 저장
    print("4. CSV 파일 저장 중...")
    final_df.to_csv(OUTPUT_FILE, index=False, encoding='utf-8-sig')
    print(f"🎉 [완료] '{OUTPUT_FILE}' 생성됨.")

    # 5. 검증 (중요: 정말 30개씩인지 확인)
    print("\n📊 검증 (선수별 경기 수 확인):")
    counts = final_df['player_id'].value_counts()
    if (counts == MATCHES_PER_PLAYER).all():
        print(f"✅ 성공: 모든 선수({len(counts)}명)가 정확히 {MATCHES_PER_PLAYER}개의 기록을 가짐.")
    else:
        print("❌ 실패: 일부 선수의 경기 수가 30개가 아님.")
        print(counts.value_counts())

if __name__ == "__main__":
    generate_exact_records()