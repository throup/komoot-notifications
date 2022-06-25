Now, for the challenge:

Imagine you are a backend developer at komoot. We are discussing how we can make new users feel more welcome and help
them understand that they just joined a platform of like-minded outdoor enthusiasts. We came up with the idea of sending
every new user a notification saying
`"Hi ((user)), welcome to komoot. ((new_user_0)), ((new_user_1)) and ((new_user_2)) also joined recently."`

This is where you come into the picture. Your job is to take over the implementation of this new feature. We discussed
some requirements for the solution:

* Consume a SNS topic that sends notifications about new user signups
  (`arn:aws:sns:eu-west-1:963797398573:challenge-backend-signups`). This is what a message payload looks like:
```
    {
        "name": "Marcus",
        "id": 1589278470,
        "created_at": "2020-05-12T16:11:54.000"
    }
```

* POST this payload to our endpoint that simulates a push notification sending service
  (`https://notification-backend-challenge.main.komoot.net/`)
```
    {
        "sender": "{your@mail.com}",
        "receiver": 1589278470,
        "message": "Hi Marcus, welcome to komoot. Lise, Anna and Stephen also joined recently.",
        "recent_user_ids": [627362498, 1093883245, 304390273]
    }
```

* Compile a list of recent users if available. Make sure it is a good mix and not always the same users.


You are free to pick the approach and technology you like. Our only constraint is to deploy it on AWS for at least two
hours. It is possible to do this using the [AWS Free Tier](https://aws.amazon.com/free/). Contact us if you need another
solution. From our experience, an excellent engineer takes a few hours for the solution to be completed.

When you're done, send us all code and instructions needed to deploy and run your system. Please give us a bit of
context to understand your system and why you came up with your approach. We'll check your requests to our endpoint to
see how your solution works.

Challenge accepted?