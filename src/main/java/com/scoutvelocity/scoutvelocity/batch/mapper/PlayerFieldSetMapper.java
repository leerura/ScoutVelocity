package com.scoutvelocity.scoutvelocity.batch.mapper;

import com.scoutvelocity.scoutvelocity.batch.dto.PlayerDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PlayerFieldSetMapper implements FieldSetMapper<PlayerDto> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public PlayerDto mapFieldSet(FieldSet fs) throws BindException {
        PlayerDto p = new PlayerDto();
        try {
            p.setPlayerId(rS(fs, "playerId")); p.setPlayerUrl(rS(fs, "playerUrl"));
            p.setFifaVersion(rD(fs, "fifaVersion")); p.setFifaUpdate(rD(fs, "fifaUpdate"));
            p.setUpdateAsOf(rLD(fs, "updateAsOf")); p.setShortName(rS(fs, "shortName"));
            p.setLongName(rS(fs, "longName")); p.setPlayerPositions(rS(fs, "playerPositions"));
            p.setOverall(rI(fs, "overall")); p.setPotential(rI(fs, "potential"));
            p.setValueEur(rD(fs, "valueEur")); p.setWageEur(rD(fs, "wageEur"));
            p.setAge(rI(fs, "age")); p.setDob(rLD(fs, "dob"));
            p.setHeightCm(rI(fs, "heightCm")); p.setWeightKg(rI(fs, "weightKg"));
            p.setClubTeamId(rD(fs, "clubTeamId")); p.setClubName(rS(fs, "clubName"));
            p.setLeagueId(rD(fs, "leagueId")); p.setLeagueName(rS(fs, "leagueName"));
            p.setLeagueLevel(rD(fs, "leagueLevel")); p.setClubPosition(rS(fs, "clubPosition"));
            p.setClubJerseyNumber(rD(fs, "clubJerseyNumber")); p.setClubLoanedFrom(rS(fs, "clubLoanedFrom"));
            p.setClubJoinedDate(rLD(fs, "clubJoinedDate"));
            p.setClubContractValidUntilYear(rID(fs, "clubContractValidUntilYear"));
            p.setNationalityId(rD(fs, "nationalityId")); p.setNationalityName(rS(fs, "nationalityName"));
            p.setNationTeamId(rD(fs, "nationTeamId")); p.setNationPosition(rS(fs, "nationPosition"));
            p.setNationJerseyNumber(rD(fs, "nationJerseyNumber")); p.setPreferredFoot(rS(fs, "preferredFoot"));
            p.setWeakFoot(rI(fs, "weakFoot")); p.setSkillMoves(rI(fs, "skillMoves"));
            p.setInternationalReputation(rI(fs, "internationalReputation")); p.setWorkRate(rS(fs, "workRate"));
            p.setBodyType(rS(fs, "bodyType")); p.setRealFace(rS(fs, "realFace"));
            p.setReleaseClauseEur(rD(fs, "releaseClauseEur")); p.setPlayerTags(rS(fs, "playerTags"));
            p.setPlayerTraits(rS(fs, "playerTraits"));
            p.setPace(rID(fs, "pace")); p.setShooting(rID(fs, "shooting"));
            p.setPassing(rID(fs, "passing")); p.setDribbling(rID(fs, "dribbling"));
            p.setDefending(rID(fs, "defending")); p.setPhysic(rID(fs, "physic"));
            p.setAttackingCrossing(rI(fs, "attackingCrossing"));
            p.setAttackingFinishing(rI(fs, "attackingFinishing"));
            p.setAttackingHeadingAccuracy(rI(fs, "attackingHeadingAccuracy"));
            p.setAttackingShortPassing(rI(fs, "attackingShortPassing"));
            p.setAttackingVolleys(rI(fs, "attackingVolleys"));
            p.setSkillDribbling(rI(fs, "skillDribbling")); p.setSkillCurve(rI(fs, "skillCurve"));
            p.setSkillFkAccuracy(rI(fs, "skillFkAccuracy")); p.setSkillLongPassing(rI(fs, "skillLongPassing"));
            p.setSkillBallControl(rI(fs, "skillBallControl"));
            p.setMovementAcceleration(rI(fs, "movementAcceleration"));
            p.setMovementSprintSpeed(rI(fs, "movementSprintSpeed"));
            p.setMovementAgility(rI(fs, "movementAgility"));
            p.setMovementReactions(rI(fs, "movementReactions"));
            p.setMovementBalance(rI(fs, "movementBalance"));
            p.setPowerShotPower(rI(fs, "powerShotPower")); p.setPowerJumping(rI(fs, "powerJumping"));
            p.setPowerStamina(rI(fs, "powerStamina")); p.setPowerStrength(rI(fs, "powerStrength"));
            p.setPowerLongShots(rI(fs, "powerLongShots"));
            p.setMentalityAggression(rI(fs, "mentalityAggression"));
            p.setMentalityInterceptions(rI(fs, "mentalityInterceptions"));
            p.setMentalityPositioning(rI(fs, "mentalityPositioning"));
            p.setMentalityVision(rI(fs, "mentalityVision"));
            p.setMentalityPenalties(rI(fs, "mentalityPenalties"));
            p.setMentalityComposure(rID(fs, "mentalityComposure"));
            p.setDefendingMarkingAwareness(rI(fs, "defendingMarkingAwareness"));
            p.setDefendingStandingTackle(rI(fs, "defendingStandingTackle"));
            p.setDefendingSlidingTackle(rI(fs, "defendingSlidingTackle"));
            p.setGoalkeepingDiving(rI(fs, "goalkeepingDiving"));
            p.setGoalkeepingHandling(rI(fs, "goalkeepingHandling"));
            p.setGoalkeepingKicking(rI(fs, "goalkeepingKicking"));
            p.setGoalkeepingPositioning(rI(fs, "goalkeepingPositioning"));
            p.setGoalkeepingReflexes(rI(fs, "goalkeepingReflexes"));
            p.setGoalkeepingSpeed(rI(fs, "goalkeepingSpeed"));
            p.setLs(rS(fs, "ls")); p.setSt(rS(fs, "st")); p.setRs(rS(fs, "rs")); p.setLw(rS(fs, "lw"));
            p.setLf(rS(fs, "lf")); p.setCf(rS(fs, "cf")); p.setRf(rS(fs, "rf")); p.setRw(rS(fs, "rw"));
            p.setLam(rS(fs, "lam")); p.setCam(rS(fs, "cam")); p.setRam(rS(fs, "ram")); p.setLm(rS(fs, "lm"));
            p.setLcm(rS(fs, "lcm")); p.setCm(rS(fs, "cm")); p.setRcm(rS(fs, "rcm")); p.setRm(rS(fs, "rm"));
            p.setLwb(rS(fs, "lwb")); p.setLdm(rS(fs, "ldm")); p.setCdm(rS(fs, "cdm")); p.setRdm(rS(fs, "rdm"));
            p.setRwb(rS(fs, "rwb")); p.setLb(rS(fs, "lb")); p.setLcb(rS(fs, "lcb")); p.setCb(rS(fs, "cb"));
            p.setRcb(rS(fs, "rcb")); p.setRb(rS(fs, "rb")); p.setGk(rS(fs, "gk"));
            return p;
        } catch (Exception e) { throw new BindException(p, "target"); }
    }

    private String rS(FieldSet fs, String n) {
        String v = fs.readString(n);
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }

    private Long rL(FieldSet fs, String n) {
        try {
            String v = fs.readString(n);
            return (v == null || v.trim().isEmpty()) ? null : Long.parseLong(v.trim());
        } catch (NumberFormatException e) { return null; }
    }

    private Integer rI(FieldSet fs, String n) {
        try {
            String v = fs.readString(n);
            return (v == null || v.trim().isEmpty()) ? null : Integer.parseInt(v.trim());
        } catch (NumberFormatException e) { return null; }
    }

    private Integer rID(FieldSet fs, String n) {
        try {
            String v = fs.readString(n);
            if (v == null || v.trim().isEmpty()) return null;
            Double d = Double.parseDouble(v.trim());
            return Integer.valueOf(d.intValue());
        } catch (NumberFormatException e) { return null; }
    }

    private Double rD(FieldSet fs, String n) {
        try {
            String v = fs.readString(n);
            return (v == null || v.trim().isEmpty()) ? null : Double.parseDouble(v.trim());
        } catch (NumberFormatException e) { return null; }
    }

    private LocalDate rLD(FieldSet fs, String n) {
        try {
            String v = fs.readString(n);
            return (v == null || v.trim().isEmpty()) ? null : LocalDate.parse(v.trim(), DATE_FORMATTER);
        } catch (Exception e) { return null; }
    }
}
