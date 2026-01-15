package com.scoutvelocity.scoutvelocity.batch.mapper;

import com.scoutvelocity.scoutvelocity.batch.dto.PlayerDto;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PlayerFieldSetMapper extends AbstractFieldSetMapper<PlayerDto> {

    @Override
    public PlayerDto mapFieldSet(FieldSet fs) throws BindException {
        PlayerDto p = new PlayerDto();
        try {
            p.setPlayerId(readString(fs, "playerId")); p.setPlayerUrl(readString(fs, "playerUrl"));
            p.setFifaVersion(readDouble(fs, "fifaVersion")); p.setFifaUpdate(readDouble(fs, "fifaUpdate"));
            p.setUpdateAsOf(readLocalDate(fs, "updateAsOf")); p.setShortName(readString(fs, "shortName"));
            p.setLongName(readString(fs, "longName")); p.setPlayerPositions(readString(fs, "playerPositions"));
            p.setOverall(readInteger(fs, "overall")); p.setPotential(readInteger(fs, "potential"));
            p.setValueEur(readDouble(fs, "valueEur")); p.setWageEur(readDouble(fs, "wageEur"));
            p.setAge(readInteger(fs, "age")); p.setDob(readLocalDate(fs, "dob"));
            p.setHeightCm(readInteger(fs, "heightCm")); p.setWeightKg(readInteger(fs, "weightKg"));
            p.setClubTeamId(readDouble(fs, "clubTeamId")); p.setClubName(readString(fs, "clubName"));
            p.setLeagueId(readDouble(fs, "leagueId")); p.setLeagueName(readString(fs, "leagueName"));
            p.setLeagueLevel(readDouble(fs, "leagueLevel")); p.setClubPosition(readString(fs, "clubPosition"));
            p.setClubJerseyNumber(readDouble(fs, "clubJerseyNumber")); p.setClubLoanedFrom(readString(fs, "clubLoanedFrom"));
            p.setClubJoinedDate(readLocalDate(fs, "clubJoinedDate"));
            p.setClubContractValidUntilYear(readIntegerFromDouble(fs, "clubContractValidUntilYear"));
            p.setNationalityId(readDouble(fs, "nationalityId")); p.setNationalityName(readString(fs, "nationalityName"));
            p.setNationTeamId(readDouble(fs, "nationTeamId")); p.setNationPosition(readString(fs, "nationPosition"));
            p.setNationJerseyNumber(readDouble(fs, "nationJerseyNumber")); p.setPreferredFoot(readString(fs, "preferredFoot"));
            p.setWeakFoot(readInteger(fs, "weakFoot")); p.setSkillMoves(readInteger(fs, "skillMoves"));
            p.setInternationalReputation(readInteger(fs, "internationalReputation")); p.setWorkRate(readString(fs, "workRate"));
            p.setBodyType(readString(fs, "bodyType")); p.setRealFace(readString(fs, "realFace"));
            p.setReleaseClauseEur(readDouble(fs, "releaseClauseEur")); p.setPlayerTags(readString(fs, "playerTags"));
            p.setPlayerTraits(readString(fs, "playerTraits"));
            p.setPace(readIntegerFromDouble(fs, "pace")); p.setShooting(readIntegerFromDouble(fs, "shooting"));
            p.setPassing(readIntegerFromDouble(fs, "passing")); p.setDribbling(readIntegerFromDouble(fs, "dribbling"));
            p.setDefending(readIntegerFromDouble(fs, "defending")); p.setPhysic(readIntegerFromDouble(fs, "physic"));
            p.setAttackingCrossing(readInteger(fs, "attackingCrossing"));
            p.setAttackingFinishing(readInteger(fs, "attackingFinishing"));
            p.setAttackingHeadingAccuracy(readInteger(fs, "attackingHeadingAccuracy"));
            p.setAttackingShortPassing(readInteger(fs, "attackingShortPassing"));
            p.setAttackingVolleys(readInteger(fs, "attackingVolleys"));
            p.setSkillDribbling(readInteger(fs, "skillDribbling")); p.setSkillCurve(readInteger(fs, "skillCurve"));
            p.setSkillFkAccuracy(readInteger(fs, "skillFkAccuracy")); p.setSkillLongPassing(readInteger(fs, "skillLongPassing"));
            p.setSkillBallControl(readInteger(fs, "skillBallControl"));
            p.setMovementAcceleration(readInteger(fs, "movementAcceleration"));
            p.setMovementSprintSpeed(readInteger(fs, "movementSprintSpeed"));
            p.setMovementAgility(readInteger(fs, "movementAgility"));
            p.setMovementReactions(readInteger(fs, "movementReactions"));
            p.setMovementBalance(readInteger(fs, "movementBalance"));
            p.setPowerShotPower(readInteger(fs, "powerShotPower")); p.setPowerJumping(readInteger(fs, "powerJumping"));
            p.setPowerStamina(readInteger(fs, "powerStamina")); p.setPowerStrength(readInteger(fs, "powerStrength"));
            p.setPowerLongShots(readInteger(fs, "powerLongShots"));
            p.setMentalityAggression(readInteger(fs, "mentalityAggression"));
            p.setMentalityInterceptions(readInteger(fs, "mentalityInterceptions"));
            p.setMentalityPositioning(readInteger(fs, "mentalityPositioning"));
            p.setMentalityVision(readInteger(fs, "mentalityVision"));
            p.setMentalityPenalties(readInteger(fs, "mentalityPenalties"));
            p.setMentalityComposure(readIntegerFromDouble(fs, "mentalityComposure"));
            p.setDefendingMarkingAwareness(readInteger(fs, "defendingMarkingAwareness"));
            p.setDefendingStandingTackle(readInteger(fs, "defendingStandingTackle"));
            p.setDefendingSlidingTackle(readInteger(fs, "defendingSlidingTackle"));
            p.setGoalkeepingDiving(readInteger(fs, "goalkeepingDiving"));
            p.setGoalkeepingHandling(readInteger(fs, "goalkeepingHandling"));
            p.setGoalkeepingKicking(readInteger(fs, "goalkeepingKicking"));
            p.setGoalkeepingPositioning(readInteger(fs, "goalkeepingPositioning"));
            p.setGoalkeepingReflexes(readInteger(fs, "goalkeepingReflexes"));
            p.setGoalkeepingSpeed(readInteger(fs, "goalkeepingSpeed"));
            p.setLs(readString(fs, "ls")); p.setSt(readString(fs, "st")); p.setRs(readString(fs, "rs")); p.setLw(readString(fs, "lw"));
            p.setLf(readString(fs, "lf")); p.setCf(readString(fs, "cf")); p.setRf(readString(fs, "rf")); p.setRw(readString(fs, "rw"));
            p.setLam(readString(fs, "lam")); p.setCam(readString(fs, "cam")); p.setRam(readString(fs, "ram")); p.setLm(readString(fs, "lm"));
            p.setLcm(readString(fs, "lcm")); p.setCm(readString(fs, "cm")); p.setRcm(readString(fs, "rcm")); p.setRm(readString(fs, "rm"));
            p.setLwb(readString(fs, "lwb")); p.setLdm(readString(fs, "ldm")); p.setCdm(readString(fs, "cdm")); p.setRdm(readString(fs, "rdm"));
            p.setRwb(readString(fs, "rwb")); p.setLb(readString(fs, "lb")); p.setLcb(readString(fs, "lcb")); p.setCb(readString(fs, "cb"));
            p.setRcb(readString(fs, "rcb")); p.setRb(readString(fs, "rb")); p.setGk(readString(fs, "gk"));
            return p;
        } catch (Exception e) { throw new BindException(p, "target"); }
    }
}
