#!/bin/sh

cat \
    play/public/bower_components/jquery/dist/jquery.js \
    play/public/bower_components/angular/angular.js \
    play/public/bower_components/materialize/dist/js/materialize.js \
    play/public/bower_components/angular-materialize/src/angular-materialize.js \
    > play/app/assets/javascripts/libs.js
