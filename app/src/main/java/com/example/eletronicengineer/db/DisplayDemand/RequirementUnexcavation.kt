package com.example.eletronicengineer.distributionFileSave

import java.io.Serializable

class RequirementUnexcavation(
    var id:String,
    var requirementType:String,
    var requirementVariety:String,
    var projectName:String,
    var projectSite:String,
    var projectTime:String,
    var diameterStandardBranchesNumber:String,
    var holeStandardBranchesNumber:String,
    var workerExperience:String,
    var minAgeDemand:String,
    var maxAgeDemand:String,
    var sexDemand:String,
    var roomBoardStandard:String,
    var journeyCarFare:String,
    var journeySalary:String,
    var needPileFoundation:String,
    var salaryStandard:String,
    var vehicle:String,
    var otherMachineEquipment:String,
    var additonalExplain:String,
    var name:String,
    var phone:String,
    var foundTime:String,
    var founder:String,
    var alterTime:String,
    var alterPeople:String,
    var delFlag:String,
    var version:String,
    var requirmentTeamServeId:String,
    var requirementTeamProjectList:List<requirementTeamProjectList>?,
    var requirementListQuotations:List<requirementTeamProjectList>?,
    var requirementConstructionWorkKind:String,
    var photoPath:String,
    var validTime:String
): Serializable