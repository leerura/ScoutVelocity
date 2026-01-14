import pandas as pd

def extract_and_save(df, subset_col, rename_map, filename):
    """
    공통 추출 로직 (DRY 원칙 적용)
    - 결측치 제거, 중복 제거, 정수형 변환, 컬럼명 변경, 저장
    """
    # 1. 데이터 복사 및 결측치 제거
    data = df[list(rename_map.keys())].dropna(subset=[subset_col]).copy()
    
    # 2. 중복 제거
    data = data.drop_duplicates(subset=[subset_col])
    
    # 3. ID 컬럼 정수형 변환
    data[subset_col] = data[subset_col].astype(int)
    
    # 4. 외래키(FK)나 레벨 같은 추가 숫자 컬럼 정수형 변환 (존재 시)
    for col in data.columns:
        if col != subset_col and pd.api.types.is_float_dtype(data[col]):
            # FK가 NaN이 없는 경우에만 변환
            if data[col].notna().all():
                data[col] = data[col].astype(int)

    # 5. 컬럼명 변경 (다이어그램 스키마 반영)
    data = data.rename(columns=rename_map)
    
    # 6. 정렬 및 저장
    data = data.sort_values('id')
    data.to_csv(filename, index=False)
    print(f"Saved {filename}: {len(data)} rows")

def main():
    # CSV 로드
    df = pd.read_csv('players_24_season.csv')

    # 1. League 추출
    # 필요한 컬럼: league_id, league_name, league_level
    extract_and_save(
        df, 
        subset_col='league_id', 
        rename_map={
            'league_id': 'id', 
            'league_name': 'name', 
            'league_level': 'level'
        }, 
        filename='leagues.csv'
    )

    # 2. Club 추출
    # 필요한 컬럼: club_team_id, club_name, league_id (FK)
    extract_and_save(
        df, 
        subset_col='club_team_id', 
        rename_map={
            'club_team_id': 'id', 
            'club_name': 'name', 
            'league_id': 'league_id'
        }, 
        filename='clubs.csv'
    )

    # 3. Nationality 추출
    # 필요한 컬럼: nationality_id, nationality_name
    extract_and_save(
        df, 
        subset_col='nationality_id', 
        rename_map={
            'nationality_id': 'id', 
            'nationality_name': 'name'
        }, 
        filename='nationalities.csv'
    )

if __name__ == "__main__":
    main()