package com.example.project_ellen_kotlin.ui.dashboard

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import coil.load
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.R
import com.example.project_ellen_kotlin.databinding.FragmentDashboardBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var activity: MainActivity
    private lateinit var safeContext: Context
    private val viewModel: SharedViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
        activity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView: ImageView = view.findViewById(R.id.receipt_dashboard)

        // Observe the image Uri from ViewModel
        viewModel.imageUri.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                // Load the image into the ImageView using Coil
                imageView.load(uri)
            }
        })

        imageView.setOnClickListener{
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
        // Create a PopupMenu instance
        val popupMenu = PopupMenu(requireContext(), view, Gravity.END)
        // Inflate the menu resource
        popupMenu.menuInflater.inflate(R.menu.image_options, popupMenu.menu)
        // Set the click listener for menu items
        popupMenu.setOnMenuItemClickListener { item ->
            handleMenuItemClick(item)
            true
        }
        // Show the PopupMenu
        popupMenu.show()
    }

    private fun handleMenuItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.action_delete -> {
                // Handle delete event
                deleteImage()
            }
            R.id.action_email -> {
                // Handle email event
                setupEmail()
            }
            R.id.action_show_info -> {
                // Handle show info event
                showReceiptInfo()
            }
        }

    }

    private fun showReceiptInfo() {
        val lor = viewModel.retrieveListOfReceipts()
        val builder: AlertDialog.Builder = AlertDialog.Builder(safeContext)
        builder.setTitle("Receipt Information")
        builder.setMessage("Date: " + lor[0].getDate() +
                "\nPrice: " + lor[0].getPrice() + "\n" +
                "Purpose: " + lor[0].getPurpose() + "\n")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun setupEmail() {
        val lor = viewModel.retrieveListOfReceipts()

    }

    private fun deleteImage() {
//        viewModel.updateImageUri(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


//
//        val textView: TextView = binding.textDashboard
//        viewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}