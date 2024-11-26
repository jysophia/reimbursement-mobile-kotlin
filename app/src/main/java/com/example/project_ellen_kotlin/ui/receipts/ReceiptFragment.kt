package com.example.project_ellen_kotlin.ui.receipts

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.project_ellen_kotlin.Email
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.R
import com.example.project_ellen_kotlin.databinding.FragmentReceiptsBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel
import com.example.project_ellen_kotlin.Receipt

class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptsBinding? = null
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReceiptsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receiptList: LinearLayout = view.findViewById(R.id.receiptList)

        // Handle click event
        receiptList.setOnClickListener {
            showReceiptImage()
        }

        // Handle long-click event
        receiptList.setOnLongClickListener {
            showPopupMenu(view) // Return true to indicate the event is consumed
            true
        }

        val receiptDescription: TextView = view.findViewById(R.id.receipt_description)
        val receiptCost: TextView = view.findViewById(R.id.cost)
        val receiptDate: TextView = view.findViewById(R.id.date)

        viewModel.receipt.observe(viewLifecycleOwner, Observer { receipt ->
            updateReceiptList(receiptList, receiptDescription, receiptCost, receiptDate, receipt)
        })
    }

    private fun showReceiptImage() {
        val lor = viewModel.retrieveListOfReceipts()
        val builder: AlertDialog.Builder = AlertDialog.Builder(safeContext)

        val imageView = ImageView(safeContext).apply{
            setImageBitmap(lor[0].getImageData())

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }
        builder.setView(imageView)
        builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun updateReceiptList(receiptList: LinearLayout, description: TextView, cost: TextView, date: TextView, receipt: Receipt) {

        val textView = TextView(safeContext).apply{
            description.text = receipt.getPurpose()
            cost.text = receipt.getCost().toString()
            date.text = receipt.getDate().toString()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        receiptList.addView(textView)
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
                "\nPrice: " + lor[0].getCost() + "\n" +
                "Purpose: " + lor[0].getPurpose() + "\n")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun setupEmail() {
        val lor = viewModel.retrieveListOfReceipts()
        val message = "Attached is the following receipt: \n" +
                "Date: " + lor[0].getDate() + "\n" +
                "Purpose: " + lor[0].getPurpose() + "\n" +
                "Amount Paid: " + lor[0].getCost() + "\n"
        val email = Email(lor[0], "", "", message)
//        email.setMessage(message)
        viewModel.setupEmail(email)
    }

    private fun deleteImage() {
//        viewModel.updateImageUri(null)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}