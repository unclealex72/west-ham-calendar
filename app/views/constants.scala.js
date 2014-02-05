@(constants: models.Globals)
@import models.Globals._
@import argonaut._, Argonaut._, DecodeResult._
var app = angular.module('constants', []);

app.constant('Constants', @JavaScript(constants.asJson.nospaces));