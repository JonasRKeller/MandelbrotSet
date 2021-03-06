package de.jrk.mandelbrotset

import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class MandelbrotGui : JFrame() {
    init {
        setSize(800, 600)
        setLocationRelativeTo(null)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val panel = JPanel()
        panel.layout = BorderLayout()

        val canvasContainerPanel = JPanel()
        panel.add(canvasContainerPanel, BorderLayout.CENTER)

        val mandelbrotCanvas = MandelbrotCanvas()
        canvasContainerPanel.add(mandelbrotCanvas)
        canvasContainerPanel.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val size = if (canvasContainerPanel.width > canvasContainerPanel.height) canvasContainerPanel.height else canvasContainerPanel.width
                mandelbrotCanvas.setBounds((canvasContainerPanel.width - size) / 2, (canvasContainerPanel.height - size) / 2, size, size)
            }
        })

        val controlPanel = JPanel()
        val controlPanelGridBagLayout = GridBagLayout()
        controlPanel.layout = controlPanelGridBagLayout
        panel.add(controlPanel, BorderLayout.WEST)

        val maxIterationsLabel = JLabel("maxIterations")
        controlPanelGridBagLayout.setConstraints(maxIterationsLabel, gridBagConstraints(0, 0))
        controlPanel.add(maxIterationsLabel)

        val maxIterationsTextField = JTextField()
        maxIterationsTextField.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) = update()
            override fun insertUpdate(e: DocumentEvent?) = update()
            override fun removeUpdate(e: DocumentEvent?) = update()

            private fun update() {
                try {
                    mandelbrotCanvas.maxIterations = maxIterationsTextField.text.toInt()
                } catch (e: NumberFormatException) {
                }
            }

        })
        maxIterationsTextField.text = "100"
        controlPanelGridBagLayout.setConstraints(maxIterationsTextField, gridBagConstraints(1, 0, fill = GridBagConstraints.BOTH))
        controlPanel.add(maxIterationsTextField)

        val positionLabel = JLabel("position")
        controlPanelGridBagLayout.setConstraints(positionLabel, gridBagConstraints(0, 1))
        controlPanel.add(positionLabel)

        val positionTextField = JTextField()
        positionTextField.isEditable = false
        controlPanelGridBagLayout.setConstraints(positionTextField, gridBagConstraints(1, 1, fill = GridBagConstraints.BOTH))
        controlPanel.add(positionTextField)
        mandelbrotCanvas.addZoomListener({
            positionTextField.text = with(mandelbrotCanvas) { "$cX;$cY;$cWidth;$cHeight" }
        }.also { it() })

        val generateButton = JButton("generate set")
        generateButton.addActionListener {
            mandelbrotCanvas.generateMandelbrotSet()
            mandelbrotCanvas.repaint()
        }
        controlPanelGridBagLayout.setConstraints(generateButton, gridBagConstraints(0, 2, 2, fill = GridBagConstraints.BOTH))
        controlPanel.add(generateButton)


        val progressBar = JProgressBar()
        val progressBarThread = Thread {
            while (true) {
                progressBar.value = (mandelbrotCanvas.progress * progressBar.maximum).toInt()
                try {
                    if (mandelbrotCanvas.isGenerating) {
                        progressBar.foreground = Color(255, 150, 150)
                        Thread.sleep(100)
                    } else {
                        progressBar.foreground = Color(150, 255, 150)
                        Thread.sleep(Long.MAX_VALUE)
                    }
                } catch (e: Exception) {
                }
            }
        }
        progressBarThread.start()
        mandelbrotCanvas.addGenerateListener {
            progressBarThread.interrupt()
        }
        panel.add(progressBar, BorderLayout.SOUTH)


        add(panel)
        isVisible = true
    }


    private fun gridBagConstraints(gridx: Int = GridBagConstraints.RELATIVE,
                                   gridy: Int = GridBagConstraints.RELATIVE,
                                   gridwidth: Int = 1,
                                   gridheight: Int = 1,
                                   weightx: Double = 1.0,
                                   weighty: Double = 1.0,
                                   anchor: Int = GridBagConstraints.CENTER,
                                   fill: Int = GridBagConstraints.NONE,
                                   insets: Insets = Insets(0, 0, 0, 0),
                                   ipadx: Int = 0,
                                   ipady: Int = 0) = GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady)
}