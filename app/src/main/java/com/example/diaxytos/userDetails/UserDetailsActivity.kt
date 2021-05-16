package com.example.diaxytos.userDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.diaxytos.MainActivity
import com.example.diaxytos.R
import com.example.diaxytos.utils.*
import kotlinx.android.synthetic.main.activity_user_details.*

class UserDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<UserDetailsViewModel>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        populateAgeSpinner()
        populateGenderSpinner()

        addListenerToAgeSpinner()
        addListenerToGenderSpinner()

        addDetailsImage()

        genderEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.gender = text.toString()
        }

        detailsStartButton.setOnClickListener {
            hideButton()
            viewModel.createUsersDevice(
                onSuccess = {
                    showButton()
                    setFirstTime(this)
                    storeUsersDevice(this, viewModel.deviceId, viewModel.counter)
                    MainActivity.startActivity(this, viewModel.deviceId, viewModel.token)
                },
                onFailure = {
                    showButton()
                    longToast("Something went wrong, please check your Internet connection and try again.")
                }
            )
        }
    }

    private fun populateAgeSpinner(){
        ArrayAdapter
            .createFromResource(this, R.array.age_groups, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ageSpinner.adapter = adapter
            }
    }

    private fun populateGenderSpinner(){
        ArrayAdapter
            .createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genderSpinner.adapter = adapter
            }
    }

    private fun addListenerToAgeSpinner(){
        ageSpinner.onItemSelectedListener = SpinnerListener(this, R.array.age_groups) { selectedItem ->
            viewModel.ageGroup = selectedItem
        }
    }

    private fun addListenerToGenderSpinner(){
        genderSpinner.onItemSelectedListener = SpinnerListener(this, R.array.genders) { selectedItem ->
            if (selectedItem != "Not disclosed") {
                viewModel.gender = selectedItem
                genderEditText.visibility = View.GONE
            }
            else{
                genderEditText.visibility = View.VISIBLE
                viewModel.gender = genderEditText.text.toString()
                if (viewModel.gender == ""){
                    viewModel.gender = selectedItem
                }
            }
        }
    }

    private fun addDetailsImage(){
        Glide.with(this)
            .load(R.drawable.meet_giraffe)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(120)))
            .into(detailsImageView)
    }

    private fun showButton(){
        detailsStartButton.visibility = View.VISIBLE
        detailsProgressBar.visibility = View.GONE
    }

    private fun hideButton(){
        detailsStartButton.visibility = View.GONE
        detailsProgressBar.visibility = View.VISIBLE
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

}