package com.enterprise.appbooks.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class ImageFileUtil {

    companion object{

        suspend fun loadBitmapFromFile(path: String): Bitmap? {
            return BitmapFactory.decodeFile(path)
        }

        suspend fun saveBitmapToInternalFolder(
            context: Context,
            bitmap: Bitmap,
            folderName: String,
            fileName: String
        ): File {
            // Step 1: Create folder reference
            val folder = File(context.filesDir, folderName)

            // Step 2: Create folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs() // creates parent dirs if needed
            }

            // Step 3: Create file inside folder
            val imageFile = File(folder, fileName)

            // Step 4: Write bitmap
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out
                )
                out.flush()
            }

            return imageFile
        }

    }

}