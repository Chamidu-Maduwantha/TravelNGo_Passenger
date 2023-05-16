package com.example.travelpass

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*

class EventDecorator (private val date: Date, private val context: Context) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {

            return day!!.date == date

    }

    override fun decorate(view: DayViewFacade?) {

        val drawable: Drawable = context.resources.getDrawable(R.drawable.circle_background)
        if (view != null) {
            view.setBackgroundDrawable(drawable)

            view.addSpan(DotSpan(5f, Color.RED))
        }
    }
}