var havenondemand = require('havenondemand')
var client = new havenondemand.HODClient('41885913-8861-4edc-8b4f-878982647379', 'v1')

var text = 'I hate puppies'
var data = {text: text}

client.call('analyzesentiment', data, function(err, resp, body){
  var sentiment = body.aggregate.sentiment
  var score = body.aggregate.score
  console.log(text + "|" + sentiment + "|" + score)
})
