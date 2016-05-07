/**
 * Created by AVALON on 12/12/15.
 */
/**
 * Module dependencies
 */
var mongoose = require('mongoose');

/**
 * User schema
 */

var moodSchema = new mongoose.Schema({
  happiness: {type: Number, required: true},
  sadness: {type: Number, required: true},
  anger: {type: Number, required: true},
  surprise: {type: Number, required: true},
  timestamp: {type: String}
});

/**
 * Statics
 */
moodSchema.statics.Create = function (moodObject, callback) {
  // create the Picture
  var Mood = mongoose.model('Mood', moodSchema);
  var newMood = new Mood({
    happiness: moodObject.happiness,
    sadness: moodObject.sadness,
    anger: moodObject.anger,
    surprise: moodObject.surprise,
    timestamp: moodObject.timestamp
  });
  console.log(newMood);

  // save the user
  newMood.save(function (err) {
    // In case of any error, return using the done method
    if (err) {
      return callback(err);
    }
    // Picture save successful
    return callback(null, newMood);
  });
};

/**
 * Register userInfoSchema
 */
module.exports = mongoose.model('Mood', moodSchema);