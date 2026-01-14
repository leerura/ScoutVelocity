import pandas as pd

INPUT_FILE = 'male_players.csv'
OUTPUT_FILE = 'players_24_season.csv'
TARGET_VERSION = 24.0

def extract_season():
    try:
        df = pd.read_csv(INPUT_FILE)
        
        # 필터링
        season_24_df = df[df['fifa_version'] == TARGET_VERSION].copy()
        
        if len(season_24_df) == 0:
            print("⚠️ 경고: 추출된 데이터가 0건입니다.")
            return

        # 저장
        season_24_df.to_csv(OUTPUT_FILE, index=False, encoding='utf-8-sig')
        print(f"✅ [완료] 24시즌 선수들만 '{OUTPUT_FILE}'에 저장했습니다.")

    except FileNotFoundError:
        print(f"❌ 파일을 찾을 수 없습니다: {INPUT_FILE}")

if __name__ == "__main__":
    extract_season()