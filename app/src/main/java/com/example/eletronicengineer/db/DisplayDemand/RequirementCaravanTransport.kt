package com.example.eletronicengineer.distributionFileSave

import java.io.Serializable

class RequirementCaravanTransport(
    var id:String,
    var vipId:String,
    var requirementType:String,
    var requirementVariety:String,
    var projectName:String,
    var projectSite:String,
    var projectTime:String,
    var workerExperience:String,
    var minAgeDemand:String,
    var maxAgeDemand:String,
    var sexDemand:String,
    var roomBoardStandard:String,
    var journeyCarFare:String,
    var journeySalary:String,
    var needHorseNumber:String,
    var salaryStandard:String,
    var additonalExplain:String,
    var name:String,
    var phone:String,
    var foundTime:String,
    var founder:String,
    var alterTime:String,
    var delFlag:String,
    var version:String,
    var requirmentTeamServeId:String,
    var requirementTeamProjectList:List<requirementTeamProjectList>?,
    var requirementPowerTransformationSalary:List<requirementTeamProjectList>?,
    var requirementListQuotations:List<requirementTeamProjectList>?,
    var materialsType:String,
    var photoPath:String,
    var validTime:String
): Serializable