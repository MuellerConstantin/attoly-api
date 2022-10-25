<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Attoly Password Reset</title>
</head>
<body style="background-color: #F4F4F4;">
<table>
    <tr>
        <td style="padding: 0px 10px 0px 10px;">
            <h1 style="font-size: 1.5rem; margin: 0px">Reset your password</h1>
        </td>
    </tr>
    <tr>
        <td style="padding: 10px">
            <p style="font-size: 1rem; margin: 0px">
                Hello,<br>
                There was a request to change your password! If you did not make this request then please
                ignore this email. Otherwise, please use the reset token below to change your password.
            </p>
        </td>
    </tr>
    <tr>
        <td style="padding: 0px 10px 0px 10px;">
            Token: <span style="font-weight: bold;">${resetToken.token}</span>
        </td>
    </tr>
    <tr>
        <td style="padding: 10px">
            <p style="font-size: 1rem; margin: 0px">
                Kind Regards, your Attoly team
            </p>
        </td>
    </tr>
</table>
</body>
</html>
