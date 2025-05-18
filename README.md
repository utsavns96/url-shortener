# URL Shortener
This project aims to implement a URL Shortener using Spring Boot and DynamoDB for implementation.

---

## Prerequisites:
### 1. AWS IAM & CLI
The AWS setup for this project needs a couple of components to function properly. For brevity, I'm assuming that you already have an AWS account that is a Root user. <br>
1. Create a new user with the following permissions:
   - DynamoDBFullAccess

    *We will add more permissions as we go.*
    <br><br>To create the user:
   1. Go to the IAM console and click on `Users` in the left sidebar.
   2. In the top right corner, click on `Create User`.
   3. Enter a username and leave `Provide user access to the AWS Management Console - optional` unchecked. Then click `Next`
   4. Select a group if you already have one, or else:
      1. Click on `Create group`.
      2. Enter a meaningful group name.
      3. In Permission Policies, search for 'Dynamo', which should return a couple of policies. Select `AmazonDynamoDBFullAccess`, which is typically the first one.
      4. Click `Create group`.
   5. Click `Next` and review the options you have chosen, and add Tags if you wish to do so. Then click on `Create user`.
   6. Once the user has been created, click the on user to see more details, and go to the `Security Credentials` tab.
   7. Scroll down to find the `Access keys` section, and click on `Create access key`.
   8. Select `Command Line Interface (CLI)`. AWS will show a warning about using alternatives, suggesting to use AWS CloudShell or AWS CLI V2. Since we are creating a simple user and don't want to deal with SSO right now, we will click on the Confirmation check box, and click on `Next`.
   9. Enter a tag value to keep track of what this user is for (preferably something to do with this project), and click on `Create access key`.
   10. You will be shown the `Access key ID` and `Secret access key`. Copy these values and store them in a safe place. You will not be able to see the secret access key again, so make sure to copy it somewhere safe. You can also download the credentials as a CSV file.
   11. Click on `Close` to finish creating the access key.
<br><br>
2. Download and install the AWS CLI from [here](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)
3. Once you have the AWS CLI installed, check by running `aws --version` in your terminal.
4. Configure the AWS CLI by running `aws configure` in your terminal. You will be prompted to enter the following values:
   - **AWS Access Key ID**: Enter the access key ID you copied earlier.
   - **AWS Secret Access Key**: Enter the secret access key you copied earlier.
   - **Default region name**: Enter the region you want to use (e.g., `us-east-1`).
   - **Default output format**: Enter `json` (Leaving this blank will default it to json).

### 2. DynamoDB
1. Go to the DynamoDB console and click on `Create table` on the right side of the screen.
2. Enter a table name (e.g., `url-shortener`)
3. For 'Partition Key', enter the name `shortUrl` with type `String`.
4. Leave the table settings as 'Default'.
5. Add a tag with a meaningful Key and Value (e.g. Key = "Project", Value ="url-shortener") to keep track of your resources.
6. Click on `Create table` to create the table.
7. Once the table is created, click on 'Explore Items', make sure the correct table is selected, and then click on `Create item`.
8. In the `Attributes` section, enter the following values:
   - Attribute Name = **shortUrl**; Value = Enter a random string (e.g., `abc123`).
   - Click on 'Add new attribute' of type 'Number' and enter Attribute Name = **expiryTime**; Value = Enter the current date and time in epoch format (Get epoch from [here](https://www.epochconverter.com/) e.g., `1747531132`).
   - Click on 'Add new attribute' of type 'String' and enter Attribute Name = **originalUrl**; Value = Enter a long URL (e.g., `https://www.google.com`).
   - Click on 'Create item' to create the item.
9. Once the item has been created, you will see it in this 'Explore Items' section.

*Steps 10-12 are not necessary, but more of a 'good to have', since TTL will help keep our table size nice and small. You don't need to enable it right away, and this can be done at a later time too.*

10. Go back to the `Tables` section and click on the table you just created.
11. Scroll down to find 'Time to Live (TTL)', and click on 'Turn on'.
12. Enter **expiryTime** as the TTL attribute name and click on 'Turn on TTL'.


