# Deckbuilder API

The Deckbuilder API project is a REST-styled API for storing information about Magic The Gathering decks. Authenitcation uses the Facebook Login API, so users will need a Facebook account to use the service. Since Facebook access tokens are sent in the HTTP header, an SSL connection is highly recommended.

Due to trademark and copyright concerns, no card information is supplied.

## Server Setup

### Configuration properties

On start the server will try to resolve configuration properties at `/src/main/resources/deckbuilder/mtg/config.properties`. No default configuration is included but a blank configuration is included at `/src/main/resources/deckbuilder/mtg/config-blank.properties`. Duplicate (or change the name) to `config.properties`.

### Facebook Setup

Authentication is provided through Facebook Login so an application will need to be registered through <a href="https://developers.facebook.com/">Facebook Developers</a>. After setting up an account, click the "Create New App" button. Follow the setup for a new "Website with Facebook Login" application. Make note of the App ID and App Secret.

### Initializing the database

The server is setup to use HSQLDB, which passes connection information through the `DATABASE_URL` environment variable.

To create a database in your home directory set the following environment variable

````
export DATABASE_URL=hsqldb:file:~/.deckbuilderapi/db/dbmain;create=true
````

After the environment variable is set, the database DDL can be creating using the following

````
java deckbuilder.mtg.Main dbinit
````

### Importing Set Data

If you need to import a set of cards, the easiest way is to use the `dbimportset` command. The command takes any number of arguments which are paths to files containing JSON card data.

For example, if a set of cards needs to be imported, the following command can be executed.

````
java deckbuilder.mtg.Main dbimportsets ~/testset.json
````

Where `~/testset.json` contains the following: 

```json
{
	"language": "en",
	"abbreviation": "OBL",
	"name": "Oblivion",
	"cards": [
		{"index": "1", "name": "Guantlet of Darkness", "typeLine": "Equipment", "powerToughness": null, "castingCost": "1"},
		{"index": "2", "name": "Human", "typeLine": "Creature", "powerToughness": "1/1", "castingCost": "W"}
	]
}
```

### Default administrators

Whenever a user successfully authenticates for the first time, a user account is created. If the username is found within the default administrators, they will be given the administrator permission. As an administrator, the user is able to add or modify data within the system that is restricted to a standard user.

Open the file `/src/main/resources/deckbuilder/mtg/config.properties` and add the user's Facebook username to the `administrators` property. This is a comma-separated list so any number of users can be added.

### Starting the server

In order for the server to start, there needs to be the following environment variables setup.

<table>
	<tr>
		<td>DATABASE_URL</td>
		<td>The URL for the database in which the DDL has been installed.</td>
	</tr>
	<tr>
		<td>FACEBOOK_APP_ID</td>
		<td>The App ID of the Facebook App.</td>
	</tr>
	<tr>
		<td>FACEBOOK_SECRET</td>
		<td>The App Secret of the Facebook App.</td>
	</tr>
</table>

The server can be started using the following commands

````
java deckbuilder.mtg.Main run
````

## Requests

Making a request to the server requires the client to pass their Facebook Access Token through the `Authorization` header. An Access Token can be retrieved after performing a standard OAuth2 authentication with Facebook Login.

The format of the `Authorization` HTTP Header is as follows.

````
Authorization: Facebook-Access-Token <access token>
````

### Obtaining an Access Token

To simplify development, a basic Access Token servlet is provided. After starting the server, open a browser to the following URL.

````
http://localhost:8080/facebook/auth
````

The page should redirect to the Facebook OAuth login page. After signing-in using a valid Facebook user, the user will be redirected back to the app, which displays the Access Token to use when using the service.

## API Information

### Card Set

<table>
	<tr>
		<td>GET</td>
		<td><code>/v1/set</code></td>
		<td>Gets a list of sets</td>
	</tr>
	<tr>
		<td>GET</td>
		<td><code>/v1/set/{id}</code></td>
		<td>Gets the information about the set with the specified ID</td>
	</tr>
	<tr>
		<td>GET</td>
		<td><code>/v1/set/{id}/cards</code></td>
		<td>Gets the list of cards contained within the set with the specified ID</td>
	</tr>
	<tr>
		<td>POST</td>
		<td><code>/v1/set</code></td>
		<td>Creates a new card set. This is restricted to users in the administrator group.</td>
	</tr>
</table>

### Card

<table>
	<tr>
		<td>GET</td>
		<td><code>/v1/card/{id}</code></td>
		<td>Gets a card with the given ID</td>
	</tr>
	<tr>
		<td>POST</td>
		<td><code>/v1/card</code></td>
		<td>Creates a new card. This is restricted to users in the administrator group.</td>
	</tr>
</table>

### Deck

<table>
	<tr>
		<td>GET</td>
		<td><code>/v1/deck</code></td>
		<td>Gets a list of decks created by the current user</td>
	</tr>
	<tr>
		<td>GET</td>
		<td><code>/v1/deck/{id}</code></td>
		<td>Gets a deck with the given ID</td>
	</tr>
	<tr>
		<td>POST</td>
		<td><code>/v1/deck</code></td>
		<td>Creates a new deck. The owner of the deck must be the user requesting to create the deck, unless the user is an administrator. An administrator can create a deck with any user as the owner.</td>
	</tr>
	<tr>
		<td>POST</td>
		<td><code>/v1/deck/{id}</code></td>
		<td>Updates a deck with the given ID. The user must be the owner of the deck or an administrator.</td>
	</tr>
	<tr>
		<td>DELETE</td>
		<td><code>/v1/deck/{id}</code></td>
		<td>Deletes a deck with the given ID. The user must be the owner of the deck or an administrator.</td>
	</tr>
</table>

### Deck Card

This is an instance of a card within a deck (along with a given quantity). For example, if there are four copies of the same card found within a deck, the Deck Card will have a quantity of four.

<table>
	<tr>
		<td>POST</td>
		<td><code>/v1/deckCard</code></td>
		<td>Creates a new deck-to-card association</td>
	</tr>
</table>