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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.project_ellen_kotlin.Email
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.R
import com.example.project_ellen_kotlin.Receipt
import com.example.project_ellen_kotlin.databinding.FragmentReceiptsBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        val receiptSection : LinearLayout = view.findViewById(R.id.receiptSection)
        val receiptContainer: LinearLayout = view.findViewById(R.id.receiptContainer)
        viewModel.receipts.observe(viewLifecycleOwner, Observer { receipt ->
            createReceiptView(receipt, receiptContainer, receiptSection)
//            receiptContainer.addView(receiptView)
//            updateReceiptList(receiptContainer, receiptDescription, receiptCost, receiptDate, receipts)
        })
    }

    private fun createReceiptView(receipt: Receipt, container: LinearLayout, section: LinearLayout) {
        // Create monthly header
        val monthlyHeader = LinearLayout(safeContext).apply{
            setBackgroundColor(ContextCompat.getColor(safeContext, R.color.surfaceVariant))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 17, 12, 17)
            }
            clipChildren = false
            clipToPadding = false
            orientation = LinearLayout.VERTICAL
        }

        // Get the month
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.parse(receipt.getDate().toString())
        val calendar = Calendar.getInstance()
        calendar.time = currentDate?: Date()
        val year = calendar.get(Calendar.YEAR).toString()
        val monthIndex = calendar.get(Calendar.MONTH)
        val month = DateFormatSymbols(Locale.getDefault()).months[monthIndex]

        val monthlyText = TextView(safeContext).apply{
            text = "$month $year"
            setTextColor(ContextCompat.getColor(context, R.color.shadow))
            textSize = 15f
            setTextColor(ContextCompat.getColor(safeContext, R.color.black))
        }

        // Create a horizontal container for image and texts
        val horizontalContainer = LinearLayout(safeContext).apply{
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }

        // Create image view
        val image = ImageView(safeContext).apply {
            setImageBitmap(receipt.getImageData())
            layoutParams = LinearLayout.LayoutParams(150, 150).apply{
                setMargins(10, 0, 16, 0)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Create a separate text container for text
        val textContainer = LinearLayout(safeContext).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // CreateTextViews for description, date, and cost
        val description = TextView(safeContext).apply {
            text = receipt.getDescription()
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 10, 20, 0)
            }
            setTextColor(ContextCompat.getColor(safeContext, R.color.black))
        }

        val date = TextView(safeContext).apply {
            text = receipt.getDate().toString()
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 10, 20, 0)
            }
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        val cost = TextView(safeContext).apply {
            text = receipt.getCost().toString()
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(350, 10, 0, 0)
            }
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        val statusContainer = LinearLayout(safeContext).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val status = ImageView(safeContext).apply {
            setImageDrawable(ContextCompat.getDrawable(safeContext, R.drawable.status_pending))
            layoutParams = LinearLayout.LayoutParams(204, 84).apply{
                setMargins(350, 10, 0, 0)
            }
            scaleType = ImageView.ScaleType.FIT_END
        }

//        // Create a new LinearLayout
//        val receiptContainer = LinearLayout(safeContext).apply {
//            orientation = LinearLayout.VERTICAL
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            addView(description)
//            addView(cost)
//            addView(date)
//            background = ContextCompat.getDrawable(safeContext, R.drawable.white_background)
//        }

        // Handle click event
        container.setOnClickListener {
            showReceiptImage()
        }

        // Handle long-click event
        container.setOnLongClickListener {
            view?.let { v -> showPopupMenu(v) } // Return true to indicate the event is consumed
            true
        }

        // Add monthly header
        monthlyHeader.addView(monthlyText)

        // Add monthlyHeader to receiptSection
        section.addView(monthlyHeader)
        section.visibility = View.VISIBLE

        // Add status bar and cost to statusContainer
        statusContainer.addView(cost)
        statusContainer.addView(status)

        // Add description and date to textContainer
        textContainer.addView(description)
        textContainer.addView(date)

        // Add image and textContainer to horizontalContainer
        horizontalContainer.addView(image)
        horizontalContainer.addView(textContainer)
        horizontalContainer.addView(statusContainer)

        // Add horizontalContainer to the container
        container.addView(horizontalContainer)
        container.visibility = View.VISIBLE
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
            description.text = receipt.getDescription()
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
                "Purpose: " + lor[0].getDescription() + "\n")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun setupEmail() {
        val lor = viewModel.retrieveListOfReceipts()
        val message = "Attached is the following receipt: \n" +
                "Date: " + lor[0].getDate() + "\n" +
                "Purpose: " + lor[0].getDescription() + "\n" +
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