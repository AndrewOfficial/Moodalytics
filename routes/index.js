var express = require('express');
var router = express.Router();
var moodCol = require('../models/moods.js');
var randomNumber = Math.floor(Math.random() * 6);

/* GET home page. */
router.get('/moodOutput', function(req, res, next) {
  res.send({happiness:5});
});

router.post('/moodInput', function(req, res, next){
  console.log(req.body);
  res.send("You've successfully posted to the server");
  //moodCol.create(req.body, function(err, newMood){
  //  console.log("newMood",newMood);
  //});
});

module.exports = router;
