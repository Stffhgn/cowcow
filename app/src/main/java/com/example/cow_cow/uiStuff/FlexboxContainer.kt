package com.example.cow_cow.uiStuff

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import com.google.android.flexbox.AlignItems

// Custom FlexboxContainer class that extends FlexboxLayout
class FlexboxContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FlexboxLayout(context, attrs, defStyleAttr) {

    init {
        // Set default properties
        flexWrap = FlexWrap.WRAP
        justifyContent = JustifyContent.SPACE_EVENLY
        alignItems = AlignItems.CENTER
    }

    /**
     * Adds a button to the container with specified attributes.
     *
     * @param button The button to add to the container.
     */
    fun addButton(button: ImageButton) {
        addView(button)
    }

    /**
     * Removes all buttons or views from the container.
     */
    fun clearButtons() {
        this.removeAllViews() // This should only clear existing buttons, not hide the container.
    }


    /**
     * Adds multiple buttons to the container.
     *
     * @param buttons A list of ImageButtons to add to the container.
     */
    fun addButton(buttons: List<ImageButton>) {
        buttons.forEach { button ->
            addButton(button)
        }
    }

    /**
     * Set padding for the container dynamically.
     *
     * @param padding The padding value in pixels to apply to all sides.
     */
    fun setContainerPadding(padding: Int) {
        setPadding(padding, padding, padding, padding)
    }

    /**
     * Set visibility of the container.
     *
     * @param isVisible Boolean value to set visibility (true for VISIBLE, false for GONE).
     */
    fun setVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    /**
     * Creates layout parameters for adding views to the FlexboxContainer.
     *
     * @param width Width of the layout.
     * @param height Height of the layout.
     * @return LayoutParams object for the view.
     */
    fun createLayoutParams(width: Int, height: Int): FlexboxLayout.LayoutParams {
        return FlexboxLayout.LayoutParams(width, height).apply {
            setMargins(8, 8, 8, 8)
        }
    }

    // Add more functionality to customize this further as needed
}
