package com.example.sharememes

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var currentMemeUrl: String? = null //The url was present only in the loadmeme function.We need it everywhere.
    //The url was present only in the loadmeme function. We need it everywhere. So we need a member variable.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadMeme()
    }

    private fun loadMeme() {
        nextButton.isEnabled = false
        shareButton.isEnabled = false
        progressBar.visibility = View.VISIBLE //progress bar will be visible when function is being called
        //We will get the response very soon but glide is the one taking the time
        val url = "https://meme-api.herokuapp.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                currentMemeUrl = response.getString("url")//We only need the url from JSON response

                Glide.with(this).load(currentMemeUrl).listener(object : RequestListener<Drawable>{ 
                    //We need to implement a listener because at the time glide finishes all its task of 
                    //putting the url image into the imageView the progress bar will be hidden.
                    //RequestListener is an interface
                    override fun onResourceReady( // If all things work fine and resource is ready this function will run
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE////Progressbar will be stopped visibility if resource is ready i.e. meme is displayed successfully
                        nextButton.isEnabled = true
                        shareButton.isEnabled = true
                        return false
                    }

                    override fun onLoadFailed( //If image loading fails this function will run
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE //Progressbar will be stopped visibility if image load fails
                        return false 
                    }
                }).into(memeImageView)
            },
            Response.ErrorListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show() //It will show a toast message in case of error
            })

        // Add the request to the RequestQueue.Now we do not need queue =Volley.requestQueue anymore. We will create it using :
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun showNextMeme(view: View) {
        loadMeme()
    }

    fun shareMeme(view: View) {
        val i = Intent(Intent.ACTION_SEND)//You have different types of actions for different activities.
// For example->Intent(Intent.ACTION_CALL) //In case of contacts in phone 

        //This action is used to send data
        i.type = "text/plain"//We are going to share plain text .In case of image we would have written image/plain
        i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this meme $currentMemeUrl") //Pass data through intent
        startActivity(Intent.createChooser(i, "Share this meme with")) //We create a chooser to choose where to share our meme like instagram or whatsapp or anywhere else
        // i is the target intent that we created just  now and we can also put a meassage and then pass it to mainactivity
    }
}
