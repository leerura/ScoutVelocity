import pandas as pd

INPUT_FILE = 'male_players.csv'
TARGET_VERSION = 24.0

def check_count():
    try:
        df = pd.read_csv(INPUT_FILE)
        # fifa_version이 24.0인 데이터 필터링
        season_24_df = df[df['fifa_version'] == TARGET_VERSION]
        
        count = len(season_24_df)
        print(f"📊 [결과] 전체 데이터 중 24시즌(v{TARGET_VERSION}) 선수는 총 {count}명입니다.")
        
    except FileNotFoundError:
        print(f"❌ 파일을 찾을 수 없습니다: {INPUT_FILE}")

if __name__ == "__main__":
    check_count()