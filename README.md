# MovieQuest #

## Development Setup ##
**NOTE:** The initial project sync done by Android Studio after retrieving the code from GitHub will fail as the <code>keystore.properties</code> file required by the build is not part of the repository.

The development environment may be configured as follows:
* Clone the repository (https://github.com/ibuttimer/moviequest) from GitHib in Android Studio
* When prompted to create a Studio project, choose yes and Import project from external external model Gradle
* After the build fails, close the project
* Create an account for The Movie Db (https://www.themoviedb.org), and obtain an API key (https://developers.themoviedb.org/3/getting-started)
* In the project root folder, create a file called <code>keystore.properties</code>
* Add the following line to the file; <code>theMovieDbApiKey=*\<your API key\>*</code>, e.g. <code>theMovieDbApiKey=MyApiKeyWithNoSurroundingQuotesBracesEtc</coce>
* Reopen the project

