package example;

import ij.ImagePlus;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.util.Iterator;
import java.util.List;

import net.imagej.ImageJ;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class ConnectedComponentsTest {

	public static void main(String[] args) throws ImgIOException
	{
		intersect();
	}

	public static void intersect() throws ImgIOException
	{
		// The image [100 X 100 pixels] is downloadable at https://drive.google.com/file/d/0BzopfqjGpdu8Ukp5eXpoUEFDaEE/view?usp=sharing
		ImageJ ij = new ImageJ();
		ImgOpener imgOpener = new ImgOpener(ij.getContext());
		List<SCIFIOImgPlus < UnsignedByteType >> images = imgOpener.openImgs("/Users/jaywarrick/Pictures/TIFFS/Dot.tif", new UnsignedByteType());
		ImgLabeling<Integer, IntType> labeling1 = getLabeling(images.get(0));

		ImagePlus outer = new ImagePlus("/Users/jaywarrick/Pictures/TIFFS/Dot.tif");
		Img<UnsignedByteType> image = ImageJFunctions.wrapByte(outer);
		ImgLabeling<Integer, IntType> labeling2 = getLabeling(image);

		LabelRegions<Integer> regions1 = new LabelRegions<Integer>(labeling1);
		for(LabelRegion<Integer> region : regions1)
		{
			System.out.println("Found a region in labeling1 of size: " + region.size()); // expected size 1976
			// expected = "Found a region in labeling1 of size: 1976"
			// actual = "Found a region in labeling1 of size: 1976"
		}
		LabelRegions<Integer> regions2 = new LabelRegions<Integer>(labeling2);
		for(LabelRegion<Integer> region : regions2)
		{
			System.out.println("Found a region in labeling2 of size: " + region.size());
			// expected = "Found a region in labeling1 of size: 1976"
			// actual = no printout as the loop isn't even entered because no region was identified
		}
	}
	
	public static ImgLabeling<Integer,IntType> getLabeling(Img<UnsignedByteType> image)
	{
		long[] dimensions = new long[image.numDimensions()];
		image.dimensions(dimensions);
		final Img< IntType > indexImg = ArrayImgs.ints( dimensions );
		ImgLabeling<Integer, IntType> labeling = new ImgLabeling<Integer, IntType>(indexImg);
		ConnectedComponents.labelAllConnectedComponents(image, labeling, new LabelGenerator(), StructuringElement.EIGHT_CONNECTED);
		return labeling;
	}
}

class LabelGenerator implements Iterator<Integer>
{
	private int current = -1;

	@Override
	public boolean hasNext()
	{
		if(current < Integer.MAX_VALUE-1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public Integer next()
	{
		current = current + 1;
		return current;
	}
	
	@Override
	public void remove()
	{
		// Do nothing
	}
}
