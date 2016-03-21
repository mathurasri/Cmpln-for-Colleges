# Include the Autoloader (see "Libraries" for install instructions)
require 'vendor/autoload.php';
use Mailgun\Mailgun;

# Instantiate the client.
$mgClient = new Mailgun('key-cd4b7bc150818a5bef1c69ac0471ed81');
$domain = "sandbox990075520a2548e097c9fd759f158153.mailgun.org";

# Make the call to the client.
$result = $mgClient->sendMessage("$domain",
                  array('from'    => 'Mailgun Sandbox <postmaster@sandbox990075520a2548e097c9fd759f158153.mailgun.org>',
                        'to'      => 'Mathura <mathura1987@gmail.com>',
                        'subject' => 'Hello Mathura',
                        'text'    => 'Congratulations Mathura, you just sent an email with Mailgun!  You are truly awesome!  You can see a record of this email in your logs: https://mailgun.com/cp/log .  You can send up to 300 emails/day from this sandbox server.  Next, you should add your own domain so you can send 10,000 emails/month for free.'));
    