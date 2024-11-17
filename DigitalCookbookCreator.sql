-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Recipes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Recipes` (
  `RecipeID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `RecipeTitle` VARCHAR(100) NOT NULL,
  `RecipeDescription` VARCHAR(500) NULL,
  PRIMARY KEY (`RecipeID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Ingredients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Ingredients` (
  `IngredientID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `RecipeID` INT UNSIGNED NOT NULL,
  `Name` VARCHAR(100) NOT NULL,
  `Quantity` INT NOT NULL,
  PRIMARY KEY (`IngredientID`),
  INDEX `RecipeID_idx` (`RecipeID` ASC) VISIBLE,
  CONSTRAINT `RecipeID`
    FOREIGN KEY (`RecipeID`)
    REFERENCES `mydb`.`Recipes` (`RecipeID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Steps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Steps` (
  `StepID` INT NOT NULL,
  `RecipeID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Description` VARCHAR(300) NOT NULL,
  `StepNumber` INT NOT NULL,
  PRIMARY KEY (`StepID`),
  INDEX `RecipeID_idx` (`RecipeID` ASC) VISIBLE,
  CONSTRAINT `RecipeID`
    FOREIGN KEY (`RecipeID`)
    REFERENCES `mydb`.`Recipes` (`RecipeID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
