import pandas as pd
import sys
import numpy as np

INPUT_FILE = 'players_24_season.csv'
OUTPUT_FILE = 'players_final_100k.csv'
TARGET_COUNT = 100000
ID_COL = 'player_id'
NAME_COL_SHORT = 'short_name' # 이름 컬럼 1
NAME_COL_LONG = 'long_name'   # 이름 컬럼 2

def expand_data():
    try:
        # 1. 파일 읽기
        df = pd.read_csv(INPUT_FILE, low_memory=False)
        current_count = len(df)
        
        print(f"📂 현재 24시즌 데이터: {current_count}명")

        if current_count >= TARGET_COUNT:
            print("이미 10만 명 이상입니다.")
            return

        # 2. 부족한 수 계산
        needed = TARGET_COUNT - current_count
        print(f"🚀 {needed}명을 추가 생성합니다...")

        # 3. 데이터 복제 (Random Sampling) & 인덱스 초기화
        additional_df = df.sample(n=needed, replace=True).copy()
        additional_df.reset_index(drop=True, inplace=True)

        # 4. 접미사 생성 (예: _cp_0, _cp_1 ...)
        # numpy로 한 번만 만들어서 재사용
        suffixes = '_cp_' + np.arange(needed).astype(str)

        # 5. ID 및 이름 컬럼 변경
        # 기존 값 + 접미사 붙이기
        additional_df[ID_COL] = additional_df[ID_COL].astype(str) + suffixes
        
        # 이름도 변경 (데이터에 따라 이름이 비어있을 수도 있으니 문자열로 변환 후 처리)
        additional_df[NAME_COL_SHORT] = additional_df[NAME_COL_SHORT].astype(str) + suffixes
        additional_df[NAME_COL_LONG] = additional_df[NAME_COL_LONG].astype(str) + suffixes

        # 6. 합치기
        final_df = pd.concat([df, additional_df], ignore_index=True)

        # 7. 저장
        final_df.to_csv(OUTPUT_FILE, index=False, encoding='utf-8-sig')
        print(f"🎉 [최종 완료] '{OUTPUT_FILE}' 생성 완료!")
        print(f"   예시: {final_df.iloc[-1][NAME_COL_SHORT]} (ID: {final_df.iloc[-1][ID_COL]})")

    except FileNotFoundError:
        print(f"❌ '{INPUT_FILE}'이 없습니다. 2단계 코드를 먼저 실행해주세요.")
    except Exception as e:
        print(f"❌ 오류 발생: {e}")

if __name__ == "__main__":
    expand_data()