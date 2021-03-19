package be.vinci.pae.services;

import java.util.List;

import be.vinci.pae.domain.PictureDTO;

public interface DAOPicture {

	List<PictureDTO> selectAllPictures();
	
	List<PictureDTO> selectPicturesByType();
	
	int addPicture(PictureDTO picture);
	
	PictureDTO selectPictureByID(int id);
	
	//demander � Olivier
	PictureDTO selectPictureByID(String extention);
	
	
}
