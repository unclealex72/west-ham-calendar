var gulp = require("gulp");
var concat = require("gulp-concat");
var uglify = require("gulp-uglify");
var rename = require("gulp-rename");
var webpack = require('webpack-stream');
var streamify = require("gulp-streamify");
var sort = require("gulp-sort");
var sourcemaps = require("gulp-sourcemaps");
var templateCache = require('gulp-angular-templatecache');
var inject = require("gulp-inject");
var bowerFiles = require('main-bower-files');
var _ = require("lodash");
var del = require('del');
var sass = require('gulp-ruby-sass');
var print = require('gulp-print');

gulp.task('sass', ["fonts"], function() {
    return sass('src/app/stylesheets/*.scss')
        .pipe(gulp.dest("dist"))

});

// Concatenate vendor files
gulp.task("vendor", function() {
    var sources = _(bowerFiles())
        .concat(["src/vendor/*.js", "!bower_components/**/index.js"])
        .filter(function(name) { return name.endsWith(".js")})
        .value();
    return gulp.src(sources)    // Read the files
        .pipe(print())
        .pipe(concat("vendor.js"))          // Combine into 1 file
        .pipe(uglify())
        .pipe(rename({extname: ".min.js"})) // Rename to ng-quick-date.min.js
        .pipe(gulp.dest("dist"));           // Write minified to disk
});

gulp.task("assets", function() {
    return gulp.src("src/assets/**/*.*")
        .pipe(gulp.dest("dist/assets"));
});

// Concatenate and publish the scalajs files
gulp.task("scalajs", function() {
    var jsfiles = ["js-jsdeps.js", "js-fastopt.js", "js-opt.js", "js-launcher.js"];
    var fileIndex = function(file) {
        var path = file.path;
        var name = path.substring(path.lastIndexOf("/"));
        return jsfiles.indexOf(name);
    };
    var comparator = function(file1, file2) {
        return fileIndex(file1) - fileIndex(file2);
    };
    return gulp.src(["../../js/target/scala-2.11/*.js"])
        .pipe(sort(comparator))
        .pipe(concat("app.js"))
        .pipe(gulp.dest("dist"));
});

gulp.task("templates", function() {
    return gulp.src('src/app/templates/*.html')
        .pipe(templateCache({ module: "hammersCalendar"}))
        .pipe(uglify())                     // Minify
        .pipe(rename({extname: ".min.js"})) // Rename to ng-quick-date.min.js
        .pipe(gulp.dest("dist"));
});

gulp.task("inject", ["sass", "templates", "vendor", "scalajs"], function() {
    var _inject = function (glob, tag) {
        return inject(
            gulp.src("dist/"+ glob, {
                cwd: "."
            }), {
                relative: true,
                ignorePath : "../dist",
                starttag: '<!-- inject:' + tag + ':{{ext}} -->'
            }
        );
    };
    return gulp.src("src/index.html")
        .pipe(_inject("templates*.js", "templates"))
        .pipe(_inject("vendor*.js", "vendor"))
        .pipe(_inject("main*.css", "style"))
        .pipe(_inject("app*.js", "app"))
        .pipe(gulp.dest("dist"));
});

gulp.task("clean", function() {
    return del(['dist/**']);
});

gulp.task("default", function() {
    console.log(bowerFiles());
});

gulp.task("service-worker", ["assets", "inject"], function(callback) {
    var path = require('path');
    var swPrecache = require('sw-precache');
    var rootDir = 'dist';

    swPrecache.write(path.join(rootDir, 'service-worker.js'), {
        staticFileGlobs: [rootDir + '/**'],
        verbose: true,
        stripPrefix: rootDir,
        replacePrefix: '/ui',
        runtimeCaching: [{
            urlPattern: /\/sprites\//,
            handler: 'networkFirst'
        }, {
            urlPattern: /\/entry/,
            handler: 'networkFirst'
        }]
    }, callback);
});

gulp.task("build", ["service-worker"]);

gulp.task("fonts", function() {
    var src = _(bowerFiles()).filter(function(name) { return name.indexOf("fonts") >= 0})
        .concat("bower_components/material-design-icons/iconfont/MaterialIcons-Regular.*").value();
    return gulp.src(src).pipe(gulp.dest("dist/fonts"));
});
