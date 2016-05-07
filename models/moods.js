/**
 * Created by AVALON on 12/12/15.
 */
/**
 * Module dependencies
 */
var mongoose = require('mongoose');
var bcrypt = require('bcrypt');

/**
 * User schema
 */

var moodSchema = new mongoose.Schema({
  mood: {type: Object, required: true, index: {unique: true}}
});

/**
 * Pre-save hooks
 */


/**
 * Methods
 */


/**
 * Statics
 */
moodSchema.statics.Create = function (mood, callback) {
  // create the Picture
  var Mood = mongoose.model('Mood', moodSchema);
  var newMood = new Mood({
    mood: mood
  });

  // save the user
  newMood.save(function (err) {
    // In case of any error, return using the done method
    if (err) {
      return callback(err);
    }
    console.log('saved mood in moods collection');
    // Picture save successful
    return callback(null, newMood);
  });
};

/**
 * Register userInfoSchema
 */
module.exports = mongoose.model('Mood', moodSchema);