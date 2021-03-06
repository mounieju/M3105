package fr.iut.tapawaru.action;

import java.util.ArrayList;
import fr.iut.tapawaru.map.Cell;
import fr.iut.tapawaru.map.CellPosition;
import fr.iut.tapawaru.map.Map;
import fr.iut.tapawaru.map.TypeGlyph;
import fr.iut.tapawaru.team.Buff;
import fr.iut.tapawaru.team.Character;

/**
 * Attack spells.
 * Library gathering static methods of the different attack spells.
 * Inherited class from Spell, likewise Terra spells.
 * 
 * @authors CEARD, MATHEY, MOUNIER, 
 * @authors PELLOUX-PRAYER, PRADELLE
 */
public class Attack extends Spell
{
	/**
	 * chooses a random type among the 4 adjacent glyphs
	 */
	private static TypeGlyph getAttackType(Map map, Cell cellCastingFrom)
	{
		return map.getGlyph(cellCastingFrom.getPosition().generateAdjacentGlyphPosition()[(int)(Math.random() * 4)]).getTypeGlyph();
	}

	
	private static void executeAttack(Map map, ArrayList<CellPosition> posList, TypeGlyph type)
	{
		map.getMapGui().spellAnimation(posList,type);
		switch (type)
		{
			case HOLY:
				for (CellPosition cellPos : posList)
				{
					makeItHoly(map, cellPos);
				}
				break;
				
			case FIRE:
				for (CellPosition cellPos : posList)
				{
					makeItBurn(map, cellPos);
				}
				break;
				
			case ICE:
				for (CellPosition cellPos : posList)
				{
					makeItFreeze(map, cellPos);

				}
				break;
				
			case AIR:
				for (CellPosition cellPos : posList)
				{
					makeAnAirAttack(map, cellPos);

						
				}
				break;
				
			default:
				for (CellPosition cellPos : posList)
				{
					makeANormalAttack(map, cellPos);

				}

		}
		
		
		map.getTeamController().checkWin();

	}


	private static void makeANormalAttack(Map map, CellPosition cellPos)
	{
		if (map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()) != null)
		{
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).inflict(1);
		}
	}


	private static void makeAnAirAttack(Map map, CellPosition cellPos) 
	{
		if (map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()) != null)
		{
			Character pers = map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY());
			
			if (pers.getBuff() == Buff.BURNING)
				pers.setBuff(Buff.NORMAL);
			
			pers.inflict(1);
		}
	}


	private static void makeItFreeze(Map map, CellPosition cellPos) 
	{
		if (map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()) != null)
		{
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).setBuff(Buff.FREEZING);
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).inflict(2);
		}
	}


	private static void makeItBurn(Map map, CellPosition cellPos) 
	{
		if (map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()) != null)
		{
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).setBuff(Buff.BURNING);
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).inflict(2);
		}
	}


	private static void makeItHoly(Map map, CellPosition cellPos) 
	{
		if (map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()) != null)
		{
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).heal(1);
			map.getCharacter(cellPos.getPositionX(), cellPos.getPositionY()).setBuff(Buff.NORMAL);
		}
	}

/*****************************laserbeam attack**********************************************************************/
	/**
	 * laserBeam
	 * o: caster
	 * x: target
	 * *: impact
	 * 
	 * o****x****
	 * 
	 */
	public static ArrayList<CellPosition> laserBeam(Map map, Character caster, CellPosition target, boolean execute)
	{
		ArrayList<CellPosition> cellList = new ArrayList<CellPosition>();

		makeLaserBeamAttack(map, caster, target, cellList);
		
		return cellList;
	}

/*****************************attack method for a laser beam***********************************************/

	private static void makeLaserBeamAttack(Map map, Character caster,
			CellPosition target, ArrayList<CellPosition> cellList) 
	{
		if (map.getTeamController().isDeductable(1))
		{
		
			int deltaX = target.getPositionX() - caster.getCellTraveled().getPosition().getPositionX();
			int deltaY = target.getPositionY() - caster.getCellTraveled().getPosition().getPositionY();
				
			if (Math.abs(deltaX) > Math.abs(deltaY))
			{
				makeAnHorizontalAttack(map, caster, cellList, deltaX);
	
			}
			else
			{
				makeAVerticalAttack(map, caster, cellList, deltaY);
			}
			
			executeAttack(map, cellList, getAttackType(map, caster.getCellTraveled()));
			map.getTeamController().deduct(1);
		}
	}

/******************************direction of the attack (vertical)*******************************************************/
	private static void makeAVerticalAttack(Map map, Character caster,
			ArrayList<CellPosition> cellList, int deltaY) 
	{
		if (deltaY > 0)	//down
		{
			selectCellForAnAttackDown(map, caster, cellList);
		}
		else	//up
		{
			selectCellForAnAttackUp(caster, cellList);
		}
	}


	private static void selectCellForAnAttackUp(Character caster,
			ArrayList<CellPosition> cellList) 
	{
		for (int x = caster.getCellTraveled().getPosition().getPositionX(),
			 	 y = caster.getCellTraveled().getPosition().getPositionY()-1;
			 y >= 0;
			 y--)
		{
			cellList.add(new CellPosition(x, y));
		}
	}


	private static void selectCellForAnAttackDown(Map map, Character caster,
			ArrayList<CellPosition> cellList) 
	{
		for (int x = caster.getCellTraveled().getPosition().getPositionX(),
			 	 y = caster.getCellTraveled().getPosition().getPositionY()+1;
			 y < map.getYSize();
			 y++)
		{
			cellList.add(new CellPosition(x, y));
		}
	}

/******************************direction of the attack (horizontal)*********************************************************************/
	
	private static void makeAnHorizontalAttack(Map map, Character caster,
			ArrayList<CellPosition> cellList, int deltaX) 
	{
		if (deltaX > 0) //right
		{
			selectCellForAnAttackToTheRight(map, caster, cellList);
		}
		else	//left
		{
			selectCellForAnAttackToTheLeft(caster, cellList);
		}
	}


	private static void selectCellForAnAttackToTheLeft(Character caster,
			ArrayList<CellPosition> cellList) 
	{
		for (int x = caster.getCellTraveled().getPosition().getPositionX()-1,
				y = caster.getCellTraveled().getPosition().getPositionY();
				x >= 0;
				x--)
		{
			cellList.add(new CellPosition(x, y));
		}
	}


	private static void selectCellForAnAttackToTheRight(Map map, Character caster,
			ArrayList<CellPosition> cellList) 
	{
		for (int x = caster.getCellTraveled().getPosition().getPositionX()+1,
				y = caster.getCellTraveled().getPosition().getPositionY();
				x < map.getXSize();
				x++)
		{
			cellList.add(new CellPosition(x, y));
		}
	}

/*****************************aroundCaster attack**********************************************************************/
	/**
	 * aroundCaster
	 * o: caster
	 * *: impact
	 *   ***
	 *   *o*
	 *   ***
	 */
	public static ArrayList<CellPosition> aroundCaster(Map map, Character caster, boolean execute)
	{

		ArrayList<CellPosition> cellList = new ArrayList<CellPosition>();
		
		makeAroundCasterAttack(map, caster, cellList);


		return cellList;
	}

/*****************************attack method for an around attack**********************************************************************/

	private static void makeAroundCasterAttack(Map map, Character caster,
			ArrayList<CellPosition> cellList) 
	{
		if (map.getTeamController().isDeductable(1))
		{
			int xCaster = caster.getCellTraveled().getPosition().getPositionX();
			int yCaster = caster.getCellTraveled().getPosition().getPositionY();
		
			selectCellForArroundAttack(map, cellList, xCaster, yCaster);
		
			executeAttack(map, cellList, getAttackType(map, caster.getCellTraveled()));
			map.getTeamController().deduct(1);

		}
	}

/********************************direction of the attack*******************************************************************/
	
	private static void selectCellForArroundAttack(Map map,
			ArrayList<CellPosition> cellList, int xCaster, int yCaster)
	{
		for (int y = yCaster - 1 ; y <= yCaster + 1 ; y++)
		{
			for (int x = xCaster - 1 ; x <= xCaster + 1 ; x++)
			{
				if ((x != xCaster || y != yCaster) &&
					x >= 0 && y >= 0 &&
					x < map.getXSize() && y < map.getYSize())
					cellList.add(new CellPosition(x, y));
			}
		}
	}

/*****************************flowerbomb attack**********************************************************************/
	
	/**
	 * flowerBomb
	 * o: caster
	 * X: target/impact
	 * *: impact
	 *      *
	 * o   *X*
	 *      *
	 */
	public static ArrayList<CellPosition> flowerBomb(Map map, Character caster, CellPosition target, boolean execute)
	{
		
		ArrayList<CellPosition> cellList = new ArrayList<CellPosition>();
		
		makeFlowerBombAttack(map, caster, target, cellList);


		return cellList;
	}

/*****************************attack method for a flower bomb attack**********************************************************************/

	private static void makeFlowerBombAttack(Map map, Character caster,
		CellPosition target, ArrayList<CellPosition> cellList) 
	{
		if (map.getTeamController().isDeductable(1))
		{
			selectCellForCrossAttack(map, target, cellList);
		
			executeAttack(map, cellList, getAttackType(map, caster.getCellTraveled()));
			map.getTeamController().deduct(1);

		}
	}

/******************************direction of the attack*********************************************************************/
	
	private static void selectCellForCrossAttack(Map map, CellPosition target,
			ArrayList<CellPosition> cellList) 
	{
		int x, y,
			xTarget = target.getPositionX(),
			yTarget = target.getPositionY();
		
		x = xTarget; y = yTarget+1;
		if (x >= 0 && y >= 0 &&
			x < map.getXSize() && y < map.getYSize())
			cellList.add(new CellPosition(x, y));
		
		x = xTarget; y = yTarget-1;
		if (x >= 0 && y >= 0 &&
			x < map.getXSize() && y < map.getYSize())
			cellList.add(new CellPosition(x, y));
		
		x = xTarget+1; y = yTarget;
		if (x >= 0 && y >= 0 &&
			x < map.getXSize() && y < map.getYSize())
			cellList.add(new CellPosition(x, y));
		
		x = xTarget-1; y = yTarget;
		if (x >= 0 && y >= 0 &&
				x < map.getXSize() && y < map.getYSize())
			cellList.add(new CellPosition(x, y));
		
		x = xTarget; y = yTarget;
		if (x >= 0 && y >= 0 &&
			x < map.getXSize() && y < map.getYSize())
			cellList.add(new CellPosition(x, y));
	}
}
