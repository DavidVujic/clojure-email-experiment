### clojure-email-experiment

Experimenting with parsing an email file using Clojure. 

#### Usage

Example, extract an attached CSV file from an email and extract the content as a stream:

``` clojure

(->> "my_email_file.eml"
      io/input-stream
      email/content-types
      (filter email/csv?)
      first
      email/content-stream)
```

#### References
Got a lot of inspiration and ideas from these repositories:

* [clojure-mail](https://github.com/owainlewis/clojure-mail)
* [simple-email](https://github.com/kisom/simple-email)

Useful Java docs:
* https://docs.oracle.com/javaee/6/api/javax/mail/internet/MimeMessage.html
* https://commons.apache.org/proper/commons-email/apidocs/org/apache/commons/mail/util/MimeMessageParser.html
