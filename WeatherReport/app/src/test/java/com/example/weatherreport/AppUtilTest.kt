package com.example.weatherreport

import android.content.Context
import com.example.weatherreport.common.AppUtil
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.BufferedInputStream
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    AppUtil::class,
    File::class,
    BufferedInputStream::class,
    BufferedWriter::class,
    InputStreamReader::class,
    BufferedWriter::class
)
class AppUtilTest {

    @Mock
    private lateinit var context: Context

    @Before
    fun setUp() {
        //TODO
    }
}