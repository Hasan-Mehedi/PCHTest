# PCHTest
• Android architecture component (Room, Live data and viewModel) is used to design the application with MVVM architecture 
pattern which gives the flexibility for unit testing.

• When the app is opened it checks with the internet to download the userdata. The data is saved in the database so the user
can edit the user data in the future.
• For the subsequent network call, when the data is loaded it checks if the user data is already downloaded by comparing the "id" saved with the sharedPreference. if the data already exist for that user, it does not re-download the data. 

• For the network operation Volley library is used.

• recyclerview is used to show the data in the recycler view.

• Material design is used to show the view. (e.g. edit text is populated using the material design)

• db folder: contains all the class with database operation. The DAO interface defines all the database operations to do on 
entity. 
Since Room doesn't allow database queries on the main thread, AsyncTasks is used to execute them asynchronously. 

• adapter Folder: adapter class is used for loading the user data into recycler view item

• repository folder: userrepository class is used to abstracts the data layer from the rest of the app and mediates 
between different data sources, like a web service and a local cache. It hides the different database operations 
(like SQLite queries) and provides a clean API to the ViewModel. The user repository first provides what’s persisted while 
requesting the network for fresh data.

Once new data arrives the repository is responsible from updating the local database.

• viewModel folder:The ViewModel class allows data to survive configuration changes such as screen rotations. In the viewModel class Live data is used so the updated data is observed immediately by the observers and the user data in the recycler view is updated immediately

