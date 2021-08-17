package com.rex.lifetracker.utils

import android.content.Context
import android.util.Log
import com.rex.lifetracker.model.FireBaseModel.EmergencyModel.LocalEmergencyModel
import com.rex.lifetracker.utils.Constant.TAG
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellValue
import org.apache.poi.xssf.usermodel.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat

object ExcelReader {
    private var tracker = 0
    private var infoList = ArrayList<LocalEmergencyModel>()
    private var valueList = ArrayList<String>()
    private lateinit var inputStream:FileInputStream
    private lateinit var workbook:XSSFWorkbook
    private lateinit var sheet:XSSFSheet
    private lateinit var formulaEvaluator: XSSFFormulaEvaluator
    private lateinit var cell: XSSFCell
    private lateinit var cellValue: CellValue
    fun readxl(context: Context,stateName: String,thana:String): ArrayList<LocalEmergencyModel> {//file name + thana name


        try {

            val file = File(context.getExternalFilesDir("EXCEL"), "$stateName.xlsx")
            inputStream = FileInputStream(file)
            workbook = XSSFWorkbook(inputStream)
            sheet = workbook.getSheetAt(0)
            val rowsCount = sheet.physicalNumberOfRows
            formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()

            //outter loop, loops through rows
            for (r in 1 until rowsCount) {
                tracker = 0
                val row = sheet.getRow(r)
                val cellCount = row.physicalNumberOfCells

                while (tracker < cellCount) {
                    val value = getCellAsString(row, tracker, formulaEvaluator)
                    if (thana == "All over Thana"){
                        valueList.add(value)
                    }else{
                        if (tracker == 0 && value != thana) {
                            tracker = 0
                            break
                        }else{
                            valueList.add(value)
                        }
                    }

//                    val cellInfo = "row: $r, cell: $tracker,  Value: $value\n"
//                    Log.d(TAG, "Result:  $cellInfo")
                    tracker++
                }
                if (tracker > 0) {
                    infoList.add(
                        LocalEmergencyModel(
                            valueList[0],
                            valueList[1],
                            valueList[2],
                            valueList[3],
                            valueList[4],
                            valueList[5]
                        )
                    )
                    valueList.clear()
                }
            }

            return infoList

        } catch (e: Exception) {
            Log.d(TAG, "readxl: exception $e")
            return infoList

        }
    }
    private fun getCellAsString(
        row: XSSFRow?,
        c: Int,
        formulaEvaluator: XSSFFormulaEvaluator?
    ): String {
        var value = ""
        try {
             cell = row!!.getCell(c)
             cellValue = formulaEvaluator!!.evaluate(cell)
            when (cellValue.cellType) {
                Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.booleanValue
                Cell.CELL_TYPE_NUMERIC -> {
                    val numericValue = cellValue.numberValue
                    value = if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val date = cellValue.numberValue
                        val formatter = SimpleDateFormat("MM/dd/yy")
                        formatter.format(HSSFDateUtil.getJavaDate(date))
                    } else {
                        "" + numericValue
                    }
                }
                Cell.CELL_TYPE_STRING -> value = "" + cellValue.stringValue
                else -> {
                }
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, "getCellAsString: NullPointerException: " + e.message)
        }
        return value!!
    }
}