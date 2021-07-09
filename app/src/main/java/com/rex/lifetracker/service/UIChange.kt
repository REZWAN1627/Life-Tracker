package com.rex.lifetracker.service

sealed class UIChange{
    object START : UIChange()
    object END : UIChange()
}