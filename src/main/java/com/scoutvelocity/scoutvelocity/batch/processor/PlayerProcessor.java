package com.scoutvelocity.scoutvelocity.batch.processor;

import com.scoutvelocity.scoutvelocity.batch.dto.PlayerDto;
import com.scoutvelocity.scoutvelocity.domain.club.Club;
import com.scoutvelocity.scoutvelocity.domain.club.ClubInfo;
import com.scoutvelocity.scoutvelocity.domain.club.repository.ClubRepository;
import com.scoutvelocity.scoutvelocity.domain.nationality.Nationality;
import com.scoutvelocity.scoutvelocity.domain.nationality.NationalityInfo;
import com.scoutvelocity.scoutvelocity.domain.nationality.repository.NationalityRepository;
import com.scoutvelocity.scoutvelocity.domain.player.*;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PlayerProcessor implements ItemProcessor<PlayerDto, Player> {
    
    private final ClubRepository clubRepository;
    private final NationalityRepository nationalityRepository;
    
    @Override
    public Player process(PlayerDto dto) throws Exception {
        Club club = null;
        if (dto.getClubTeamId() != null) {
            Long clubId = dto.getClubTeamId().longValue();
            club = clubRepository.findById(clubId).orElse(null);
        }
        
        Long nationalityId = dto.getNationalityId().longValue();
        Nationality nationality = nationalityRepository.findById(nationalityId)
                .orElseThrow(() -> new IllegalArgumentException("Nationality not found: " + nationalityId));
        
        return Player.builder()
                .id(dto.getPlayerId())
                .shortName(dto.getShortName())
                .longName(dto.getLongName())
                .playerPositions(dto.getPlayerPositions())
                .overall(dto.getOverall())
                .potential(dto.getPotential())
                .age(dto.getAge())
                .dateOfBirth(dto.getDob())
                .valueEur(toBigDecimal(dto.getValueEur()))
                .wageEur(toBigDecimal(dto.getWageEur()))
                .releaseClauseEur(toBigDecimal(dto.getReleaseClauseEur()))
                .club(club)
                .clubInfo(buildClubInfo(dto))
                .nationality(nationality)
                .nationalityInfo(buildNationalityInfo(dto))
                .physical(buildPhysicalInfo(dto))
                .stats(buildPlayerStats(dto))
                .fifaVersion(toInt(dto.getFifaVersion()))
                .fifaUpdate(toInt(dto.getFifaUpdate()))
                .updateAsOf(dto.getUpdateAsOf())
                .realFace(dto.getRealFace())
                .playerTags(dto.getPlayerTags())
                .playerTraits(dto.getPlayerTraits())
                .build();
    }
    
    private ClubInfo buildClubInfo(PlayerDto dto) {
        return ClubInfo.builder()
                .position(dto.getClubPosition())
                .jerseyNumber(toInt(dto.getClubJerseyNumber()))
                .joinedDate(dto.getClubJoinedDate())
                .contractValidUntilYear(dto.getClubContractValidUntilYear())
                .loanedFrom(dto.getClubLoanedFrom())
                .build();
    }
    
    private NationalityInfo buildNationalityInfo(PlayerDto dto) {
        return NationalityInfo.builder()
                .nationTeamId(dto.getNationTeamId() != null ? dto.getNationTeamId().longValue() : null)
                .position(dto.getNationPosition())
                .jerseyNumber(toInt(dto.getNationJerseyNumber()))
                .build();
    }
    
    private PhysicalInfo buildPhysicalInfo(PlayerDto dto) {
        return PhysicalInfo.builder()
                .heightCm(dto.getHeightCm())
                .weightKg(dto.getWeightKg())
                .bodyType(dto.getBodyType())
                .preferredFoot(dto.getPreferredFoot())
                .weakFoot(dto.getWeakFoot())
                .skillMoves(dto.getSkillMoves())
                .workRate(dto.getWorkRate())
                .internationalReputation(dto.getInternationalReputation())
                .build();
    }
    
    private PlayerStats buildPlayerStats(PlayerDto dto) {
        return PlayerStats.builder()
                .pace(dto.getPace())
                .shooting(dto.getShooting())
                .passing(dto.getPassing())
                .dribbling(dto.getDribbling())
                .defending(dto.getDefending())
                .physic(dto.getPhysic())
                .attacking(AttackingStats.builder()
                        .crossing(dto.getAttackingCrossing())
                        .finishing(dto.getAttackingFinishing())
                        .headingAccuracy(dto.getAttackingHeadingAccuracy())
                        .shortPassing(dto.getAttackingShortPassing())
                        .volleys(dto.getAttackingVolleys())
                        .build())
                .skill(SkillStats.builder()
                        .dribbling(dto.getSkillDribbling())
                        .curve(dto.getSkillCurve())
                        .fkAccuracy(dto.getSkillFkAccuracy())
                        .longPassing(dto.getSkillLongPassing())
                        .ballControl(dto.getSkillBallControl())
                        .build())
                .movement(MovementStats.builder()
                        .acceleration(dto.getMovementAcceleration())
                        .sprintSpeed(dto.getMovementSprintSpeed())
                        .agility(dto.getMovementAgility())
                        .reactions(dto.getMovementReactions())
                        .balance(dto.getMovementBalance())
                        .build())
                .power(PowerStats.builder()
                        .shotPower(dto.getPowerShotPower())
                        .jumping(dto.getPowerJumping())
                        .stamina(dto.getPowerStamina())
                        .strength(dto.getPowerStrength())
                        .longShots(dto.getPowerLongShots())
                        .build())
                .mentality(MentalityStats.builder()
                        .aggression(dto.getMentalityAggression())
                        .interceptions(dto.getMentalityInterceptions())
                        .positioning(dto.getMentalityPositioning())
                        .vision(dto.getMentalityVision())
                        .penalties(dto.getMentalityPenalties())
                        .composure(dto.getMentalityComposure())
                        .build())
                .defendingStats(DefendingStats.builder()
                        .markingAwareness(dto.getDefendingMarkingAwareness())
                        .standingTackle(dto.getDefendingStandingTackle())
                        .slidingTackle(dto.getDefendingSlidingTackle())
                        .build())
                .goalkeeping(GoalkeepingStats.builder()
                        .diving(dto.getGoalkeepingDiving())
                        .handling(dto.getGoalkeepingHandling())
                        .kicking(dto.getGoalkeepingKicking())
                        .positioning(dto.getGoalkeepingPositioning())
                        .reflexes(dto.getGoalkeepingReflexes())
                        .speed(dto.getGoalkeepingSpeed())
                        .build())
                .build();
    }
    
    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
    
    private Integer toInt(Double value) {
        return value != null ? value.intValue() : null;
    }
}
