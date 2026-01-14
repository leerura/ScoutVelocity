import pandas as pd
import sys

# === 설정 (이 부분만 본인 CSV에 맞게 수정하세요) ===
INPUT_CSV = 'male_players.csv'          # 원본 파일명
OUTPUT_CSV = 'players_24.csv'      # 결과 파일명 (24시즌만 남긴 것)

SEASON_COLUMN = 'season'           # 시즌 정보가 있는 컬럼명 (예: season, year 등)
TARGET_SEASON = 24                 # 추출하고 싶은 시즌 값 (숫자 24 또는 문자 '24')
# =================================================

def filter_season_players():
    try:
        # 1. CSV 읽기
        df = pd.read_csv(INPUT_CSV)
        print(f"📂 전체 데이터 로드: {len(df)}건")

        # 2. 컬럼 확인 (Fail-Fast)
        if SEASON_COLUMN not in df.columns:
            print(f"❌ [오류] '{SEASON_COLUMN}' 컬럼이 CSV에 없습니다.")
            print(f"   현재 컬럼 목록: {list(df.columns)}")
            sys.exit(1)

        # 3. 데이터 타입 통일 및 필터링
        # CSV에서 읽으면 숫자가 문자로 인식될 수도 있으므로, 비교를 위해 문자열로 변환하여 처리
        df[SEASON_COLUMN] = df[SEASON_COLUMN].astype(str)
        target_str = str(TARGET_SEASON)

        filtered_df = df[df[SEASON_COLUMN] == target_str]
        
        count = len(filtered_df)
        
        if count == 0:
            print(f"⚠️ 경고: 시즌 값이 '{target_str}'인 데이터가 0건입니다.")
            print("   CSV 파일의 시즌 컬럼 값을 확인해보세요. (예: '2024', '23/24' 등)")
            return

        # 4. 저장
        filtered_df.to_csv(OUTPUT_CSV, index=False, encoding='utf-8-sig')
        print(f"✅ 추출 완료! 24시즌 선수 {count}건을 '{OUTPUT_CSV}'에 저장했습니다.")

    except FileNotFoundError:
        print(f"❌ 파일을 찾을 수 없습니다: {INPUT_CSV}")

if __name__ == "__main__":
    filter_season_players()