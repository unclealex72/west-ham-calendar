#!/bin/sh

cat \
    play/public/bower_components/jquery/dist/jquery.min.js \
    play/public/bower_components/angular/angular.min.js \
    play/public/bower_components/materialize/dist/js/materialize.min.js \
    play/public/bower_components/angular-materialize/src/angular-materialize.js \
    play/public/bower_components/angular-vs-repeat/src/angular-vs-repeat.js \
    > play/public/javascripts/libs.js
