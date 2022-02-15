package edu.drury.mandelbrotnavigator;

import edu.drury.mandelbrotnavigator.color.Fire;
import edu.drury.mandelbrotnavigator.math.MandelbrotMath;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MandelbrotNavigator implements ActionListener, PropertyChangeListener, ListSelectionListener {
	private static final double DEFAULT_X = -0.5;
	private static final double DEFAULT_Y = 0;
	private static final double DEFAULT_SCALE = 2.75;
	private static final int DEFAULT_CYCLES = Math.min((int) (1 / DEFAULT_SCALE * 500), 1000);
	private static final double DEFAULT_LIMIT = 2.0;

	private double scale = DEFAULT_SCALE;
	private double x = DEFAULT_X;
	private double y = DEFAULT_Y;
	private int cycles = DEFAULT_CYCLES;
	private double limit = DEFAULT_LIMIT;

	private final ArrayList<Bookmark> bookmarks = new ArrayList<>();

	// Level 0 in container hierarchy
	private final JFrame frame = new JFrame();
	// Level 1
	private final JPanel panelContent = new JPanel();
	// Level 2
	private final JSplitPane splitPane = new JSplitPane();
	// Level 3
	private final MainPanel panelMain = new MainPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	// Level 4
	private final JPanel panelSide = new JPanel();
	// Level 5
	private final JPanel panelPosition = new JPanel();
	private final JPanel panelColors = new JPanel();
	private final JPanel panelGeneration = new JPanel();
	private final JPanel panelBookmarks = new JPanel();
	private final JPanel panelExport = new JPanel();
	private final JPanel panelVerticalSpacer = new JPanel();
	// Level 4
	// - Position
	private final JButton positionButtonZoomIn = new JButton();
	private final JButton positionButtonZoomOut = new JButton();
	private final JLabel positionLabelX = new JLabel();
	private final JFormattedTextField positionFieldX = new JFormattedTextField();
	private final JLabel positionLabelY = new JLabel();
	private final JFormattedTextField positionFieldY = new JFormattedTextField();
	private final JLabel positionLabelScale = new JLabel();
	private final JFormattedTextField positionFieldScale = new JFormattedTextField();
	private final JButton positionButtonReset = new JButton();
	// - Colors
	private final JComboBox<String> colorsComboBox = new JComboBox<>();
	// - Generation
	private final JLabel generationLabelLimit = new JLabel();
	private final JFormattedTextField generationFieldLimit = new JFormattedTextField();
	private final JLabel generationLabelCycles = new JLabel();
	private final JFormattedTextField generationFieldCycles = new JFormattedTextField();
	private final JButton generationButtonReset = new JButton();
	// - Bookmarks
	private final JButton bookmarksButtonSave = new JButton();
	private final JList<String> bookmarksList = new JList<>();
	private final JButton bookmarksButtonGoTo = new JButton();
	private final JButton bookmarksButtonRemove = new JButton();
	// - Export
	private final JButton exportButtonSave = new JButton();

	private class MainPanel extends JPanel {
		private int row = 0;
		private BufferedImage image;
		private int width = 0;
		private int height = 0;
		private double left = 0;
		private double top = 0;
		private double step = 0;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (row == 0) {
				width = getWidth();
				height = getHeight();
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				left = x - (1.0 * width / height) * scale / 2;
				top = y + scale / 2;
				step = scale / height;
			}

			if (row < height) {
				row = Math.min(row, height - 32);

				for (int y = row; y < row + 32; y++) {
					for (int x = 0; x < width; x++) {
						int value = MandelbrotMath.getMandelbrotValue(left + x * step, top - y * step, cycles, limit);
						int[] rgb = Fire.getColor(value, cycles);
						image.setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
					}
				}

				row += 32;
				super.repaint();
			} else {
				row = 0;
			}

			g.drawImage(image, 0, 0, null);
		}

		@Override
		public void repaint() {
			row = 0;
			super.repaint();
		}
	}

	private static class Bookmark implements Serializable {
		private final String name;
		private final double x;
		private final double y;
		private final double scale;

		public Bookmark(String name, double x, double y, double scale) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.scale = scale;
		}

		@Override
		public String toString() {
			return "Bookmark{" + "name='" + name + '\'' + ", x=" + x + ", y=" + y + ", scale=" + scale + '}';
		}
	}

	private MandelbrotNavigator() {
		/* Frame */ {
			frame.setTitle("Mandelbrot");
			frame.setContentPane(panelContent);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		/* Content Panel */ {
			panelContent.setLayout(new BorderLayout());
			panelContent.setPreferredSize(new Dimension(1600, 1000));
			panelContent.add(splitPane, BorderLayout.CENTER);
		}

		/* Split Pane */ {
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(scrollPane);
			splitPane.setRightComponent(panelMain);
		}

		/* Scroll Pane */ {
			scrollPane.setViewportView(panelSide);
			scrollPane.setPreferredSize(new Dimension(256, -1));
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}

		/* Main Panel */ {}

		/* Side Panel */ {
			panelSide.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelSide.add(panelPosition, gridBagConstraints);

			gridBagConstraints.gridy++;

			panelSide.add(panelColors, gridBagConstraints);

			gridBagConstraints.gridy++;

			panelSide.add(panelGeneration, gridBagConstraints);

			gridBagConstraints.gridy++;

			panelSide.add(panelBookmarks, gridBagConstraints);

			gridBagConstraints.gridy++;

			panelSide.add(panelExport, gridBagConstraints);

			gridBagConstraints.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridy++;

			panelSide.add(panelVerticalSpacer, gridBagConstraints);
		}

		/* Position Panel */ {
			panelPosition.setLayout(new GridBagLayout());
			panelPosition.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Position"));

			positionButtonZoomIn.setText("Zoom in");
			positionButtonZoomIn.setActionCommand("positionZoomIn");
			positionButtonZoomIn.addActionListener(this);

			positionButtonZoomOut.setText("Zoom out");
			positionButtonZoomOut.setActionCommand("positionZoomOut");
			positionButtonZoomOut.addActionListener(this);

			positionLabelX.setText("X:");

			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			numberFormat.setMaximumFractionDigits(22);

			positionFieldX.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
			positionFieldX.setValue(x);
			positionFieldX.addPropertyChangeListener("value", this);

			positionLabelY.setText("Y:");

			positionFieldY.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
			positionFieldY.setValue(y);
			positionFieldY.addPropertyChangeListener("value", this);

			positionLabelScale.setText("Scale:");

			positionFieldScale.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
			positionFieldScale.setValue(scale);
			positionFieldScale.addPropertyChangeListener("value", this);

			positionButtonReset.setText("Reset");
			positionButtonReset.setActionCommand("positionReset");
			positionButtonReset.addActionListener(this);

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelPosition.add(positionButtonZoomIn, gridBagConstraints);

			gridBagConstraints.gridy = 1;

			panelPosition.add(positionButtonZoomOut, gridBagConstraints);

			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridx = 0;

			panelPosition.add(positionLabelX, gridBagConstraints);

			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;

			panelPosition.add(positionFieldX, gridBagConstraints);

			gridBagConstraints.gridy = 3;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridx = 0;

			panelPosition.add(positionLabelY, gridBagConstraints);

			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;

			panelPosition.add(positionFieldY, gridBagConstraints);

			gridBagConstraints.gridy = 4;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridx = 0;

			panelPosition.add(positionLabelScale, gridBagConstraints);

			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;

			panelPosition.add(positionFieldScale, gridBagConstraints);

			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;

			panelPosition.add(positionButtonReset, gridBagConstraints);
		}

		/* Colors Panel */ {
			panelColors.setLayout(new GridBagLayout());
			panelColors.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Colors"));

			colorsComboBox.addItem("Fire");
			colorsComboBox.addItem("RGB");
			colorsComboBox.setSelectedIndex(0);
			// TODO: Retrieve colors combo box items dynamically

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelColors.add(colorsComboBox, gridBagConstraints);
		}

		/* Generation Panel */ {
			panelGeneration.setLayout(new GridBagLayout());
			panelGeneration.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Generation"));

			generationLabelLimit.setText("Limit:");

			generationFieldLimit.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getNumberInstance())));
			generationFieldLimit.setValue(limit);
			generationFieldLimit.addPropertyChangeListener("value", this);

			generationLabelCycles.setText("Cycles:");

			generationFieldCycles.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));
			generationFieldCycles.setValue(cycles);
			generationFieldCycles.addPropertyChangeListener("value", this);

			generationButtonReset.setText("Reset");
			generationButtonReset.setActionCommand("generationReset");
			generationButtonReset.addActionListener(this);

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelGeneration.add(generationLabelLimit, gridBagConstraints);

			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;

			panelGeneration.add(generationFieldLimit, gridBagConstraints);

			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy++;

			panelGeneration.add(generationLabelCycles, gridBagConstraints);

			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;

			panelGeneration.add(generationFieldCycles, gridBagConstraints);

			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy++;

			panelGeneration.add(generationButtonReset, gridBagConstraints);
		}

		/* Bookmarks Panel */ {
			panelBookmarks.setLayout(new GridBagLayout());
			panelBookmarks.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Bookmarks"));

			bookmarksButtonSave.setText("Save position");
			bookmarksButtonSave.setActionCommand("bookmarksSave");
			bookmarksButtonSave.addActionListener(this);

			bookmarksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			bookmarksList.addListSelectionListener(this);

			bookmarksButtonGoTo.setText("Go to");
			bookmarksButtonGoTo.setEnabled(false);
			bookmarksButtonGoTo.setActionCommand("bookmarksGoTo");
			bookmarksButtonGoTo.addActionListener(this);

			bookmarksButtonRemove.setText("Remove");
			bookmarksButtonRemove.setEnabled(false);
			bookmarksButtonRemove.setActionCommand("bookmarksRemove");
			bookmarksButtonRemove.addActionListener(this);

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelBookmarks.add(bookmarksButtonSave, gridBagConstraints);

			gridBagConstraints.gridy++;

			panelBookmarks.add(bookmarksList, gridBagConstraints);

			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridy++;

			panelBookmarks.add(bookmarksButtonGoTo, gridBagConstraints);

			gridBagConstraints.gridx = 1;

			panelBookmarks.add(bookmarksButtonRemove, gridBagConstraints);
		}

		/* Serialized Bookmarks */ {
			setJListFromIO();
		}

		/* Export Panel */ {
			panelExport.setLayout(new GridBagLayout());
			panelExport.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Export"));

			exportButtonSave.setText("Save as PNG");

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(6, 6, 6, 6);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;

			panelExport.add(exportButtonSave, gridBagConstraints);
		}

		/* End */ {
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("positionZoomIn")) {
			scale *= 0.8;
			setCycles();
			positionFieldScale.setValue(scale);
			generationFieldCycles.setValue(cycles);
			panelMain.repaint();
		} else if (e.getActionCommand().equals("positionZoomOut")) {
			scale *= 1.25;
			setCycles();
			positionFieldScale.setValue(scale);
			generationFieldCycles.setValue(cycles);
			panelMain.repaint();
		} else if (e.getActionCommand().equals("positionReset")) {
			scale = DEFAULT_SCALE;
			x = DEFAULT_X;
			y = DEFAULT_Y;
			cycles = DEFAULT_CYCLES;
			positionFieldScale.setValue(scale);
			positionFieldX.setValue(x);
			positionFieldY.setValue(y);
			generationFieldCycles.setValue(cycles);
			panelMain.repaint();
		} else if (e.getActionCommand().equals("generationReset")) {
			cycles = DEFAULT_CYCLES;
			limit = DEFAULT_LIMIT;
			generationFieldCycles.setValue(cycles);
			generationFieldLimit.setValue(limit);
			panelMain.repaint();
		} else if (e.getActionCommand().equals("bookmarksSave")) {
			ArrayList<Bookmark> bookmarks = getBookmarksFromIO();
			bookmarks.add(new Bookmark("Untitled", x, y, scale));
			setBookmarksIO(bookmarks);
			setJListFromIO();
		} else if (e.getActionCommand().equals("bookmarksGoTo")) {
			for (Bookmark bookmark : getBookmarksFromIO()) {
				if (bookmarksList.getSelectedValue().equals(bookmark.name)) {
					x = bookmark.x;
					y = bookmark.y;
					scale = bookmark.scale;
					positionFieldX.setValue(x);
					positionFieldY.setValue(y);
					positionFieldScale.setValue(scale);
					panelMain.repaint();
				}
			}
		} else if (e.getActionCommand().equals("bookmarksRemove")) {
			ArrayList<Bookmark> bookmarks = getBookmarksFromIO();
			bookmarks.removeIf(bookmark -> bookmark.name.equals(bookmarksList.getSelectedValue()));
			setBookmarksIO(bookmarks);
			setJListFromIO();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		Object source = e.getSource();
		if (source == positionFieldX) {
			x = ((Number) positionFieldX.getValue()).doubleValue();
			panelMain.repaint();
		} else if (source == positionFieldY) {
			y = ((Number) positionFieldY.getValue()).doubleValue();
			panelMain.repaint();
		} else if (source == positionFieldScale) {
			scale = ((Number) positionFieldScale.getValue()).doubleValue();
			setCycles();
			generationFieldCycles.setValue(cycles);
			panelMain.repaint();
		} else if (source == generationFieldLimit) {
			limit = ((Number) generationFieldLimit.getValue()).doubleValue();
			panelMain.repaint();
		} else if (source == generationFieldCycles) {
			cycles = ((Number) generationFieldCycles.getValue()).intValue();
			panelMain.repaint();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			if (bookmarksList.getSelectedIndex() == -1) {
				bookmarksButtonGoTo.setEnabled(false);
				bookmarksButtonRemove.setEnabled(false);
			} else {
				bookmarksButtonGoTo.setEnabled(true);
				bookmarksButtonRemove.setEnabled(true);
			}
		}
	}

	private void setCycles() {
		cycles = Math.min((int) (1 / scale * 500), 1000);
	}

	/*
	Bookmarking process:

	get bookmarks from ser and place in array
	set JList from array

	add:
		add bookmark to array
		set JList from array
		reset ser from array

	remove:
		remove bookmark from array
		set JList from array
		reset ser from array

	methods needed:
	+ get or create or set array from ser
	+ set JList from array
	+ reset ser from array
	+ add bookmark to array
	+ remove bookmark from array
	 */

	private ArrayList<Bookmark> getBookmarksFromIO() {
		ArrayList<Bookmark> bookmarks = new ArrayList<>();

		try {
			FileInputStream fis = new FileInputStream("bookmarks.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);

			while (fis.available() != 0) {
				bookmarks.add((Bookmark) ois.readObject());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return bookmarks;
	}

	private void setJListFromIO() {
		ArrayList<Bookmark> bookmarks = getBookmarksFromIO();

		String[] bookmarksNames = new String[bookmarks.size()];

		for (int i = 0; i < bookmarks.size(); i++) {
			bookmarksNames[i] = bookmarks.get(i).name;
		}

		bookmarksList.setListData(bookmarksNames);
	}

	private void setBookmarksIO(ArrayList<Bookmark> bookmarks) {
		try {
			FileOutputStream fos = new FileOutputStream("bookmarks.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.reset();

			for (Bookmark bookmark : bookmarks) {
				oos.writeObject(bookmark);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new MandelbrotNavigator();
	}
}