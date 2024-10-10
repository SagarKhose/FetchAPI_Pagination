package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Movie
import com.example.myapplication.Model.MovieResponse
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieRecyclerView: RecyclerView

    private var isLoading = false
    private var currentPage = 1
    private val totalPages = 10 // Replace with the actual total number of pages from the API
    private val movieList = mutableListOf<Movie>() // List to store all movies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieRecyclerView = findViewById(R.id.movieRecyclerView)

        // Set up RecyclerView
        movieRecyclerView.layoutManager = LinearLayoutManager(this)
        movieAdapter = MovieAdapter(movieList)
        movieRecyclerView.adapter = movieAdapter

        // Add scroll listener for pagination
        movieRecyclerView.addOnScrollListener(scrollListener)

        // Fetch Marvel movies from API
        fetchMovies("marvel", currentPage)
    }

    private fun fetchMovies(query: String, page: Int) {
        isLoading = true

        // Use CoroutineScope to launch the coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the API using Retrofit
                val response = ApiClient.movieApiService.searchMovies("577eb312", query, page)
                // Switch to Main thread to update the UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            movieList.addAll(it.Search)
                            movieAdapter.notifyDataSetChanged()
                        }
                    } else {
                        // Handle error response (e.g., log or show a message)
                        Log.e("Error", "Response not successful: ${response.errorBody()?.string()}")
                    }
                    // Set loading state to false regardless of success or failure
                    isLoading = false
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    // Set loading state to false in case of error
                    isLoading = false
                    // Optionally show a toast or log the error
                    Log.e("Error", "Failed to fetch movies: ${e.message}")
                }
            }
        }
    }

    // Function to fetch movies from the API
    /*private fun fetchMovies(query: String, page: Int) {
        isLoading = true
        ApiClient.instance.searchMovies("577eb312", query, page).enqueue(object :
            Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        movieList.addAll(it.Search) // Add new movies to the existing list
                        movieAdapter.notifyDataSetChanged() // Notify adapter about data change
                        isLoading = false
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                // Handle failure
                isLoading = false
            }
        })
    }*/

    // Scroll listener to detect when user scrolls to the end of the list
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (!isLoading && firstVisibleItemPosition + visibleItemCount >= totalItemCount && currentPage < totalPages) {
                currentPage++ // Increment the page number
                fetchMovies("marvel", currentPage) // Fetch the next page of movies
            }
        }
    }
}