package com.scoutvelocity.scoutvelocity.batch.reader;
import com.scoutvelocity.scoutvelocity.batch.config.BatchConfig;
import com.scoutvelocity.scoutvelocity.batch.dto.PlayerDto;
import com.scoutvelocity.scoutvelocity.batch.mapper.PlayerFieldSetMapper;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class PlayerReader {
    @Bean
    public FlatFileItemReader<PlayerDto> playerItemReader() {
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("playerItemReader")
                .resource(new FileSystemResource(BatchConfig.CSV_PATH + "players_final_100k.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .names("playerId","playerUrl","fifaVersion","fifaUpdate","updateAsOf","shortName","longName","playerPositions","overall","potential","valueEur","wageEur","age","dob","heightCm","weightKg","clubTeamId","clubName","leagueId","leagueName","leagueLevel","clubPosition","clubJerseyNumber","clubLoanedFrom","clubJoinedDate","clubContractValidUntilYear","nationalityId","nationalityName","nationTeamId","nationPosition","nationJerseyNumber","preferredFoot","weakFoot","skillMoves","internationalReputation","workRate","bodyType","realFace","releaseClauseEur","playerTags","playerTraits","pace","shooting","passing","dribbling","defending","physic","attackingCrossing","attackingFinishing","attackingHeadingAccuracy","attackingShortPassing","attackingVolleys","skillDribbling","skillCurve","skillFkAccuracy","skillLongPassing","skillBallControl","movementAcceleration","movementSprintSpeed","movementAgility","movementReactions","movementBalance","powerShotPower","powerJumping","powerStamina","powerStrength","powerLongShots","mentalityAggression","mentalityInterceptions","mentalityPositioning","mentalityVision","mentalityPenalties","mentalityComposure","defendingMarkingAwareness","defendingStandingTackle","defendingSlidingTackle","goalkeepingDiving","goalkeepingHandling","goalkeepingKicking","goalkeepingPositioning","goalkeepingReflexes","goalkeepingSpeed","ls","st","rs","lw","lf","cf","rf","rw","lam","cam","ram","lm","lcm","cm","rcm","rm","lwb","ldm","cdm","rdm","rwb","lb","lcb","cb","rcb","rb","gk")
                .fieldSetMapper(new PlayerFieldSetMapper())  // Custom mapper for type conversion
                .build();
    }
}