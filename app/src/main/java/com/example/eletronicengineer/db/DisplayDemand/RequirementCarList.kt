package com.example.eletronicengineer.db.DisplayDemand

import java.io.Serializable

class RequirementCarList (
                            val id :String,
                            var requirementTeamServeId:String,
                        var carType:String,
                          var needCarNumber:String,
                          var construction:String,
                          var isInsurance:String,
                          var isDriver:String,
                          var haveDriver:String,
                          var noDriver:String,
                          var remark:String
                           ): Serializable