package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Model.Movie
import com.example.myapplication.databinding.RecItemsBinding

class MovieAdapter(private val movies: MutableList<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: RecItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            // Binding data to views
            binding.movieTitle.text = movie.Title
            binding.movieYear.text = movie.Year
            binding.movieType.text = movie.Type
            Glide.with(binding.root.context)
                .load(movie.Poster)
                .into(binding.moviePoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = RecItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }


    override fun getItemCount(): Int = movies.size

    // Method to add new data to the existing list
    fun addMovies(newMovies: List<Movie>) {
        movies.addAll(newMovies)
        notifyDataSetChanged()  // Notify the adapter of the new items
    }
}