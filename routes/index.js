var express = require('express');
var router = express.Router();
var moodCol = require('../models/moods.js');
var randomNumber = Math.floor(Math.random() * 6);

/* GET home page. */
router.get('/moodOutput', function(req, res, next) {
  res.send(randomNumber);
});

router.post('/moodInput', function(req, res, next){
  console.log(req.body);
  moodCol.create({happiness:.01,sadness:.9,anger:.4,surprise:.3});
});

module.exports = router;
