# MovieQuest #

## Development Setup ##
The development environment may be configured as follows:
* Create an account for The Movie Db (https://www.themoviedb.org), and obtain an API key (https://developers.themoviedb.org/3/getting-started)
* Clone the repository (https://github.com/ibuttimer/moviequest) from GitHib in Android Studio
* When prompted to create a Studio project, choose yes and Import the project using the default Gradle wrapper.
* After the build has finished, open the <code>keystore.properties</code> file in the project root folder.
* Update the following line with a valid TMDb API key; <code>theMovieDbApiKey=*ReplaceWithYourApiKey*</code>, e.g. <code>theMovieDbApiKey=MyApiKeyWithNoSurroundingQuotesBracesEtc</coce>
* Rebuild the project

**NOTE:** If you receive '*Server access is not authorised*' errors, please verify that the TMDb API key is entered correctly in the <code>keystore.properties</code> file.



