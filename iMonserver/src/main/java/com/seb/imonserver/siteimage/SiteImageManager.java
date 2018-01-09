package com.seb.imonserver.siteimage;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.fileupload.FileItem;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SiteImageManager {
	private static final Logger LOG = LogManager.getLogger(SiteImageManager.class);

	private static SiteImageManager _instance = null;

	private String _siteImageManagerBaseDirectory;
	
	private SiteImageManager() {
	}
	
	/**
	 * Singleton DatasourceHelper
	 * 
	 * @return The DatasourceHelper to read the content of the Datasource database
	 */
	public static SiteImageManager getInstance() {
		if (_instance == null) {
			_instance = new SiteImageManager();
		}
		
		return _instance;
	}
	
	public void initialize(String siteImageManagerBaseDirectory) {
		_siteImageManagerBaseDirectory = siteImageManagerBaseDirectory;
	}
	
	public boolean saveImageForSite(String siteId, FileItem theImageItem) {

		String directoryForTheImage = getDirectoryForHiRes(siteId);
		File directory = new File(directoryForTheImage);
		if (directory.exists() == false) {
			if (directory.mkdirs() == false) {
				LOG.error("saveImageForSite::directory to store image cannot be created: " + directoryForTheImage);
				return false;
			}
		}

		String imageFileNameBase = "" + System.currentTimeMillis();
		String imageFileName = imageFileNameBase + ".jpeg";
		String fullImageName = directoryForTheImage + File.separator + imageFileName; 

		LOG.info("saveImageForSite::image will be saved : " + fullImageName);
		File uploadedFile = new File(fullImageName);
		try {
			theImageItem.write(uploadedFile);
			createThumbailFrom(siteId, uploadedFile, imageFileName);
			return true;
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
	}
	
	private void createThumbailFrom(String siteId, File sourceImage, String imageFileName) {
		try {
			String directoryForTheImage = getDirectoryForLowRes(siteId);
			File directory = new File(directoryForTheImage);
			if (directory.exists() == false) {
				if (directory.mkdirs() == false) {
					LOG.error("createThumbailFrom::directory to store image cannot be created: " + directoryForTheImage);
					return;
				}
			}

			BufferedImage img = ImageIO.read(sourceImage); // load image

			//Quality indicate that the scaling implementation should do everything
			// create as nice of a result as possible , other options like speed
			// will return result as fast as possible
			//Automatic mode will calculate the resultant dimensions according
			//to image orientation .so resultant image may be size of 50*36.if you want
			//fixed size like 50*50 then use FIT_EXACT
			//other modes like FIT_TO_WIDTH..etc also available.

			BufferedImage thumbImg = Scalr.resize(img, 
					Method.QUALITY, 
					Scalr.Mode.AUTOMATIC, 
					200, 200, 
					Scalr.OP_ANTIALIAS);

			String fullImageName = directoryForTheImage + File.separator + imageFileName; 
			File f2 = new File(fullImageName);

			LOG.info("createThumbailFrom::image thumbail will be saved : " + fullImageName);
			ImageIO.write(thumbImg, "jpg", f2);
		} catch (IOException e) {
			  LOG.error(e);
		}
	}
	
	public String[] imagesForSite(String siteId) {
		String directoryForTheImage = getDirectoryForHiRes(siteId);
		File directory = new File(directoryForTheImage);
		if (directory.exists() == false) {
			return new String[0];
		} else {
			String[] files = directory.list(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".jpeg");
			    }
			});
			
			return files;
		}
	}
	
	public byte[] imageForSite(String siteId, String imageName, String quality) {
		String directoryForTheImage = null;
		if (quality.equals("HiRes")) {
			directoryForTheImage = getDirectoryForHiRes(siteId);
		} else if (quality.equals("LowRes")) {
			directoryForTheImage = getDirectoryForLowRes(siteId);
		} else {
			directoryForTheImage = getDirectoryForLowRes(siteId);
		}

		String imageFileName = directoryForTheImage + File.separator + imageName;
		File theImageFile = new File(imageFileName);
		
		return readImageFileContent(theImageFile);
	}
	

	public byte[] defaultImageForSite(String siteId, String quality) {
		String[] imageList = this.imagesForSite(siteId);
		
		if (imageList == null || imageList.length == 0) {
			return null;
		}
		
		// Use the first part of the filename (milliseconds from 1970) to find the most recent image
		try {
			String moreRecentImageName = imageList[0];
			String baseName = FilenameUtils.getBaseName(moreRecentImageName);
			long highestTimeFromEpoch = Long.parseLong(baseName);
			for (int i = 1 ; i < imageList.length; i++) {
				baseName = FilenameUtils.getBaseName(imageList[i]);;
				long newTimeFromEpoch = Long.parseLong(baseName);
				if (newTimeFromEpoch > highestTimeFromEpoch) {
					highestTimeFromEpoch = newTimeFromEpoch;
					moreRecentImageName = imageList[i];
				}
			}
			return imageForSite(siteId, moreRecentImageName, quality);
		} catch (Exception ex) {
			  LOG.error(ex);
		}
		return null;
	}
	
	public boolean deleteImageForSite(String siteId, String imageName) {
		String imageFileNameHiRes = getDirectoryForHiRes(siteId) + File.separator + imageName;
		String imageFileNameLowRes = getDirectoryForLowRes(siteId) + File.separator + imageName;

		File imageLowRes = new File(imageFileNameLowRes);
		File imageHiRes = new File(imageFileNameHiRes);
		try {
			boolean isLowResDeleted = imageLowRes.delete();
			boolean isHiResDeleted = imageHiRes.delete();

			if (isLowResDeleted == true && isHiResDeleted == true) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			  LOG.error(ex);
		}
		return false;
	}
	
	
	private static byte[] readImageFileContent(File theImageFile) {
		// Read the image
		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) theImageFile.length()];
		try {
			//convert file into array of bytes
			fileInputStream = new FileInputStream(theImageFile);
			fileInputStream.read(bFile);
			fileInputStream.close();
		}
		catch (Exception ex)	{
			  LOG.error(ex);
		}
		return bFile;		
	}
	
	private String getDirectoryForHiRes(String siteId) {
		return _siteImageManagerBaseDirectory + File.separator + siteId + File.separator + "HiRes";
	}
	
	private String getDirectoryForLowRes(String siteId) {
		return _siteImageManagerBaseDirectory + File.separator + siteId + File.separator + "LowRes";
	}

}
