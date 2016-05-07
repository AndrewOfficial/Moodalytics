var express = require('express');
var router = express.Router();
//var moodCol = require('../models/moods.js');
var smileValues = [];
var average = 0;
var request = require('request');

var x = 0;

router.get('/moodOutput', function(req, res, next) {
});

router.post('/moodInput', function(req, res, next){
  smileValues.push(req.body.happiness);
  res.send(req.body.happiness);
});

var sendInterval = setInterval(function(){
  getAverage();
  request.post(
    'https://api.particle.io/v1/devices/270018000d47343432313031/led?access_token=cf0bb3a3b303a068ac415a12d232b98fc5afe03b',
    { json: { average: average } },
    function (error, response, body) {
      if (!error && response.statusCode == 200) {
      }
    }
  );
}, 250);

function getAverage() {
  var sum = 0;
  if (smileValues.length > 500){
    smileValues.splice(0,400);
  } else if (smileValues.length >= 100 && smileValues.length< 500) {
    for (var i; i < 100; i++) {
      sum += smileValues[i];
    }
    average = '' + Math.floor((sum / i) * 256);
  } else {
    var i = 0;
    for (i; i < smileValues.length; i++) {
      sum += smileValues[i];
    }
    average = '' + Math.floor((sum / i) * 256);
  }
}

getAverage();

module.exports = router;
