package com.catl.line.test;
/////////////////////////////////////////////////////////////////////////////// 
// Copyright (C) 2003-2014, Open Design Alliance (the "Alliance"). 
// All rights reserved. 
// 
// This software and its documentation and related materials are owned by 
// the Alliance. The software may only be incorporated into application 
// programs owned by members of the Alliance, subject to a signed 
// Membership Agreement and Supplemental Software License Agreement with the
// Alliance. The structure and organization of this software are the valuable  
// trade secrets of the Alliance and its suppliers. The software is also 
// protected by copyright law and international treaty provisions. Application  
// programs incorporating this software must include the following statement 
// with their copyright notices:
//   
//   This application incorporates Teigha(R) software pursuant to a license 
//   agreement with Open Design Alliance.
//   Teigha(R) Copyright (C) 2003-2014 by Open Design Alliance. 
//   All rights reserved.
//
// By use of this software, its documentation or related materials, you 
// acknowledge and accept the above terms.
///////////////////////////////////////////////////////////////////////////////
// This example illustrates how C++ classes can be used from Java using SWIG.
// The Java class gets mapped onto the C++ class and behaves as if it is a Java class.

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.opendesign.core.DwgVersion;
import com.opendesign.core.ExSystemServices;
import com.opendesign.core.FileAccessMode;
import com.opendesign.core.FileCreationDisposition;
import com.opendesign.core.FileShareMode;
import com.opendesign.core.OdCmEntityColor;
import com.opendesign.core.OdGeCircArc2d;
import com.opendesign.core.OdGeCurve2d;
import com.opendesign.core.OdGeDoubleArray;
import com.opendesign.core.OdGeExtents3d;
import com.opendesign.core.OdGeMatrix3d;
import com.opendesign.core.OdGePoint2d;
import com.opendesign.core.OdGePoint2dArray;
import com.opendesign.core.OdGePoint3d;
import com.opendesign.core.OdGePoint3dArray;
import com.opendesign.core.OdGeScale3d;
import com.opendesign.core.OdGeVector2d;
import com.opendesign.core.OdGeVector3d;
import com.opendesign.core.OdGeVector3dArray;
import com.opendesign.core.OdGiDrawable;
import com.opendesign.core.OdGiMaterialColor;
import com.opendesign.core.OdGiMaterialMap;
import com.opendesign.core.OdGiMaterialTraits;
import com.opendesign.core.OdGiRasterImage;
import com.opendesign.core.OdGiRasterImageDesc;
import com.opendesign.core.OdIntArray;
import com.opendesign.core.OdOleItemHandler;
import com.opendesign.core.OdResult;
import com.opendesign.core.OdRxObjectPtrArray;
import com.opendesign.core.OdStreamBuf;
import com.opendesign.core.SubentType;
import com.opendesign.td.*;


public class OdWriteExJava {
  static {
    try {
        System.loadLibrary("TeighaJavaCore");
		System.loadLibrary("TeighaJavaDwg");
    } catch (UnsatisfiedLinkError e) {
      System.out.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
	  e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String argv1[]) 
  { 
            System.out.println("\nTeighaJava sample program. Copyright (c) 2012, Open Design Alliance\n");
			
			/********************************************************************/
			/* Create a Service and HostApp instances.                          */
			/********************************************************************/
            ExSystemServices systemServices = new ExSystemServices();
            ExHostAppServices hostApp = new ExHostAppServices();
			hostApp.disableOutput(true); //Disable progress meter
			
		    /********************************************************************/
			/* Initialize Teigha.                                               */
			/********************************************************************/
            TD_Db.odInitialize(systemServices);
            String dstFileName = "D://ģ�� - ����.dwg";
			/********************************************************************/
			/* Display the Product and Version that created the executable      */
			/********************************************************************/
			System.out.format("\nOdWriteEx developed using %s ver %s\n",  hostApp.product(), hostApp.versionString());
			
			
			boolean success = true;
            try 
            {
			    /********************************************************************/
				/* Create a default OdDbDatabase object                             */
				/********************************************************************/
                OdDbDatabase db = hostApp.createDatabase();
			    OdDbDatabaseSummaryInfo sumInfo = TD_Db.oddbGetSummaryInfo(db);
				sumInfo.setComments("File was created by the Java version of OdWriteEx");
				TD_Db.oddbPutSummaryInfo(sumInfo);
				/****************************************************************/
				/* Fill the database                                                */
				/****************************************************************/
				DbFiller filler = new DbFiller();
				filler.fillDatabase(db);
                /********************************************************************/
                /* Write the OdDgDatabase data into the DGN file                    */
                /********************************************************************/
                if (null != db) {
					System.out.println("write database to file");
					// was DwgVersion.vAC24 before
                    db.writeFile(dstFileName, SaveType.kDwg, DwgVersion.vAC12, true);
                } else {
                    System.out.println("db is null");
                }

            }
            /********************************************************************/
            /* Display the error                                                */
            /********************************************************************/
            catch (Error Err)
            {
                System.out.println("Error2" + Err);
            }
            hostApp.delete();

            //TD_Db.odUninitialize();
			//System.out.println("Teigha uninitialized\n");
  }
  
  
  private static class DbFiller 
  {
    OdDbObjectIdArray m_layoutEntities = new OdDbObjectIdArray();
    List<OdGeCurve2d> m_edgeReferences = new ArrayList<OdGeCurve2d>(); // keep references from GC
	
  	private EntityBoxes m_EntityBoxes = new EntityBoxes();
	  private double m_textSize		  = 0.2;
	  private OdGeVector3d m_textOffset = new OdGeVector3d(0.5 * m_textSize, -0.5 * m_textSize,0.0);
	  private OdGeVector3d m_textLine   = new  OdGeVector3d(0.0, -1.6 * m_textSize, 0.0);
    /************************************************************************/
    /* Prefix a file name with the Current Directory                        */
    /************************************************************************/
    static String inCurrentFolder(String fileName)
    {
      if ((fileName.indexOf('\\') == -1) && (fileName.indexOf('/') == -1))
      {
        File dir1 = new File (".");
        try
        {
          String sPath = dir1.getCanonicalPath();
          //sPath.TrimEnd(new char[] { '\\', '/' });
          //sPath += '/';
          sPath += fileName;
          return sPath;
        }
        catch(Exception e) 
        {
          e.printStackTrace();
          return "";
        }
      }
      else
      {
        return fileName;
      }
    }
    /************************************************************************/
    /* Append a PolygonMesh vertex to the specified PolygonMesh             */
    /************************************************************************/
    void appendPgMeshVertex(OdDbPolygonMesh pPgMesh, OdGePoint3d pos)
    {
      /**********************************************************************/
      /* Append a Vertex to the PolyFaceMesh                                */
      /**********************************************************************/
      OdDbPolygonMeshVertex pVertex = OdDbPolygonMeshVertex.createObject();
      pPgMesh.appendVertex(pVertex);

      /**********************************************************************/
      /* Set the properties                                                 */
      /**********************************************************************/
      pVertex.setPosition(pos);
    }

    private boolean addRegApp(OdDbDatabase pDb, String name)
    {
      return pDb.newRegApp(name);
    }

    /************************************************************************/
    /* Add a Linetype to the specified database                             */
    /*                                                                      */
    /* The symbol table and symbol table record are implicitly closed when  */
    /* this function returns.                                               */
    /************************************************************************/
    OdDbObjectId addLinetype(OdDbDatabase pDb, String name, String comments)
    {
      /**********************************************************************/
      /* Open the Linetype table                                            */
      /**********************************************************************/
      OdDbLinetypeTable pLinetypes = OdDbLinetypeTable.cast(pDb.getLinetypeTableId().safeOpenObject(OpenMode.kForWrite));
      OdDbLinetypeTableRecord pLinetype = OdDbLinetypeTableRecord.createObject();

      /**********************************************************************/
      /* Linetype must have a name before adding it to the table.           */
      /**********************************************************************/
      pLinetype.setName(name);

      /**********************************************************************/
      /* Add the record to the table.                                       */
      /**********************************************************************/
      OdDbObjectId linetypeId = pLinetypes.add(pLinetype);

      /**********************************************************************/
      /* Add the Comments.                                                  */
      /**********************************************************************/
      pLinetype.setComments(comments);

      return linetypeId;
    }

    /************************************************************************/
    /* Add Several linetypes to the specified database                      */
    /************************************************************************/
    void addLinetypes(OdDbDatabase pDb, OdDbObjectId shapeStyleId, OdDbObjectId txtStyleId)
    {
      /**********************************************************************/
      /* Continuous linetype                                                */
      /**********************************************************************/
      addLinetype(pDb, "Continuous2", "Solid Line");

      /**********************************************************************/
      /* Hidden linetype                                                    */
      /* This is not the standard Hidden linetype, but is used by examples  */
      /**********************************************************************/
      OdDbObjectId ltId = addLinetype(pDb, "Hidden", "- - - - - - - - - - - - - - - - - - - - -");
      OdDbLinetypeTableRecord pLt = OdDbLinetypeTableRecord.cast(ltId.safeOpenObject(OpenMode.kForWrite));
      pLt.setNumDashes(2);
      pLt.setPatternLength(0.1875);
      pLt.setDashLengthAt(0, 0.125);
      pLt.setDashLengthAt(1, -0.0625);

      /**********************************************************************/
      /* Linetype with text                                                 */
      /**********************************************************************/
      ltId = addLinetype(pDb, "HW_ODA", "__ HW __ OD __ HW __ OD __");
      pLt = OdDbLinetypeTableRecord.cast(ltId.safeOpenObject(OpenMode.kForWrite));
      pLt.setNumDashes(6);
      pLt.setPatternLength(1.8);
      pLt.setDashLengthAt(0, 0.5);
      pLt.setDashLengthAt(1, -0.2);
      pLt.setDashLengthAt(2, -0.2);
      pLt.setDashLengthAt(3, 0.5);
      pLt.setDashLengthAt(4, -0.2);
      pLt.setDashLengthAt(5, -0.2);

      pLt.setShapeStyleAt(1, txtStyleId);
      pLt.setShapeOffsetAt(1, new OdGeVector2d(-0.1, -0.05));
      pLt.setTextAt(1, "HW");
      pLt.setShapeScaleAt(1, 0.5);

      pLt.setShapeStyleAt(4, txtStyleId);
      pLt.setShapeOffsetAt(4, new OdGeVector2d(-0.1, -0.05));
      pLt.setTextAt(4, "OD");
      pLt.setShapeScaleAt(4, 0.5);

      /**********************************************************************/
      /* ZIGZAG linetype                                                    */
      /**********************************************************************/
      ltId = addLinetype(pDb, "ZigZag", "/\\/\\/\\/\\/\\/\\/\\/\\");
      pLt = OdDbLinetypeTableRecord.cast(ltId.safeOpenObject(OpenMode.kForWrite));
      pLt.setNumDashes(4);
      pLt.setPatternLength(0.8001);
      pLt.setDashLengthAt(0, 0.0001);
      pLt.setDashLengthAt(1, -0.2);
      pLt.setDashLengthAt(2, -0.4);
      pLt.setDashLengthAt(3, -0.2);

      pLt.setShapeStyleAt(1, shapeStyleId);
      pLt.setShapeOffsetAt(1, new OdGeVector2d(-0.2, 0.0));
      pLt.setShapeNumberAt(1, 131); //ZIG shape
      pLt.setShapeScaleAt(1, 0.2);

      pLt.setShapeStyleAt(2, shapeStyleId);
      pLt.setShapeOffsetAt(2, new OdGeVector2d(0.2, 0.0));
      pLt.setShapeNumberAt(2, 131); //ZIG shape
      pLt.setShapeScaleAt(2, 0.2);
      pLt.setShapeRotationAt(2, 3.1415926);
    }
    /************************************************************************/
    /* Add a point entity with the specified attributes to the specified    */
    /* BlockTableRecord                                                     */
    /************************************************************************/
    OdDbObjectId addPointEnt(OdDbBlockTableRecord bBTR, OdGePoint3d point, OdDbObjectId layerId, OdDbGroup pGroup)
    {
      /**********************************************************************/
      /* Create the point object                                             */
      /**********************************************************************/
      OdDbPoint pPoint = OdDbPoint.createObject();
      pPoint.setDatabaseDefaults(bBTR.database());
      OdDbObjectId pointId = bBTR.appendOdDbEntity(pPoint);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pPoint.setPosition(point);

      /**********************************************************************/
      /* Add the point to the specified group                               */
      /**********************************************************************/
      if (pGroup != null)
      {
        pGroup.append(pointId);
      }
      /**********************************************************************/
      /* Set the point to the specified layer                               */
      /**********************************************************************/
      if (!layerId.isNull())
      {
        pPoint.setLayer(layerId, false);
      }
      return pointId;
    }
    /************************************************************************/
    /* Append an XData Pair to the specified ResBuf                         */
    /************************************************************************/
    static OdResBuf appendXDataPair(OdResBuf pCurr, int code)
    {
      OdResBuf pRes = OdResBuf.createObject();
      pRes.setRestype(code);
      pCurr.setNext(pRes);
      return pCurr.next();
    }
    /************************************************************************/
    /* Add a Radial Dimension to the specified BlockTableRecord             */
    /************************************************************************/
    void addRadialDimension(OdDbObjectId btrId,
                                      int boxRow,
                                      int boxCol,
                                      OdDbObjectId layerId,
                                      OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Radial", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a circle to be dimensioned                                    */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pCircle);
      pCircle.setCenter(point.add(new OdGeVector3d(0.625, h * 3.0 / 8.0, 0)));
      pCircle.setRadius(0.5);

      /**********************************************************************/
      /* Create a Radial Dimension                                         */
      /**********************************************************************/
      OdDbRadialDimension pDimension = OdDbRadialDimension.createObject();
      pDimension.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pDimension);
      pDimension.setCenter(pCircle.center());
      OdGeVector3d chordVector = new OdGeVector3d(pCircle.radius(), 0.0, 0.0);
      chordVector.rotateBy(OdaToRadian(75.0), OdGeVector3d.getKZAxis());
      pDimension.setChordPoint(pDimension.center().add(chordVector));
      pDimension.setLeaderLength(0.125);
      pDimension.useDefaultTextPosition();
    }
    /************************************************************************/
    /* Add an Associative Dimension to the specified BlockTableRecord       */
    /************************************************************************/
    void addDimAssoc(OdDbObjectId btrId,
                               int boxRow,
                               int boxCol,
                               OdDbObjectId layerId,
                               OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Associative", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a line to be dimensioned                                    */
      /**********************************************************************/
      OdGePoint3d line1Pt = new OdGePoint3d();
      line1Pt.setX(point.getX() + (w * 1.0 / 8.0));
      line1Pt.setY(point.getY() + (h * 2.0 / 8.0));
      OdGePoint3d line2Pt = new OdGePoint3d();
      line2Pt.setX(line1Pt.getX() + 3.75);
      line2Pt.setY(point.getY() + (h * 7.0 / 8.0));

      OdDbLine pLine = OdDbLine.createObject();
      pLine.setDatabaseDefaults(pDb);
      OdDbObjectId lineId = bBTR.appendOdDbEntity(pLine);
      pLine.setStartPoint(line1Pt);
      pLine.setEndPoint(line2Pt);

      /**********************************************************************/
      /* Create a rotated dimension and dimension the ends of the line      */
      /**********************************************************************/
      OdDbRotatedDimension pDimension = OdDbRotatedDimension.createObject();
      pDimension.setDatabaseDefaults(pDb);
      OdDbObjectId dimensionId = bBTR.appendOdDbEntity(pDimension);

      OdGePoint3d dimLinePt = new OdGePoint3d();
      dimLinePt.setX(point.getX() + (w / 2.0));
      dimLinePt.setY(point.getY() + (h * 1.0 / 8.0));
      pDimension.setDatabaseDefaults(pDb);
      pDimension.setXLine1Point(pLine.startPoint());
      pDimension.setXLine2Point(pLine.endPoint());
      pDimension.setDimLinePoint(dimLinePt);
      pDimension.useDefaultTextPosition();
      pDimension.createExtensionDictionary();

      /**********************************************************************/
      /* Create an associative dimension                                    */
      /**********************************************************************/
      OdDbDimAssoc pDimAssoc = OdDbDimAssoc.createObject();

      /**********************************************************************/
      /* Associate the associative dimension with the rotated dimension by  */
      /* adding it to the extension dictionary of the rotated dimension     */
      /**********************************************************************/
      OdDbDictionary pDict = OdDbDictionary.cast(pDimension.extensionDictionary().safeOpenObject(OpenMode.kForWrite));
      OdDbObjectId dimAssId = pDict.setAt("ACAD_DIMASSOC", pDimAssoc);

      /**********************************************************************/
      /* Associate the rotated dimension with the associative dimension     */
      /**********************************************************************/
      pDimAssoc.setDimObjId(dimensionId);
      pDimAssoc.setRotatedDimType(OdDbDimAssoc.RotatedDimType.kUnknown);

      /**********************************************************************/
      /* Attach the line to the associative dimension                       */
      /**********************************************************************/
      OdDbOsnapPointRef pointRef = OdDbOsnapPointRef.createObject();
      pointRef.setPoint(pLine.startPoint());
      pointRef.setOsnapType(OsnapMode.kOsModeStart);
      pointRef.setNearPointParam(0.0);

      pointRef.mainEntity().getObjectIds().add(lineId);
      pointRef.mainEntity().subentId().setType(SubentType.kVertexSubentType);

      pDimAssoc.setPointRef((int)OdDbDimAssoc.PointType.kXline1Point.swigValue(), pointRef);

      pointRef = OdDbOsnapPointRef.createObject();
      pointRef.setPoint(pLine.endPoint());
      pointRef.setOsnapType(OsnapMode.kOsModeEnd);
      pointRef.setNearPointParam(1.0);

      pointRef.mainEntity().getObjectIds().add(lineId);
      pointRef.mainEntity().subentId().setType(SubentType.kEdgeSubentType);

      pDimAssoc.setPointRef((int)OdDbDimAssoc.PointType.kXline2Point.swigValue(), pointRef);

      /**********************************************************************/
      /* Add Persistent reactors from the rotated dimension and the line    */
      /* to the associative dimension                                       */
      /**********************************************************************/
      pDimension.addPersistentReactor(dimAssId);
      pLine.addPersistentReactor(dimAssId);
    }
    /************************************************************************/
    /* Add a 3 Point Angular Dimension to the specified BlockTableRecord    */
    /************************************************************************/
    void add3PointAngularDimension(OdDbObjectId btrId,
                                             int boxRow,
                                             int boxCol,
                                             OdDbObjectId layerId,
                                             OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "3 Point Angular", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create an arc to be dimensioned                                    */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pArc);
      OdGePoint3d center = point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0));
      pArc.setCenter(center);
      pArc.setStartAngle(OdaToRadian(0.0));
      pArc.setEndAngle(OdaToRadian(90.0));
      pArc.setRadius(w * 3.0 / 8.0);

      /**********************************************************************/
      /* Create 3 point angular dimension                                   */
      /**********************************************************************/
      OdDb3PointAngularDimension pDimension = OdDb3PointAngularDimension.createObject();
      pDimension.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pDimension.setCenterPoint(pArc.center());
      pDimension.setArcPoint(pArc.center().add(new OdGeVector3d(pArc.radius() + 0.45, 0.0, 0.0)));

      OdGePoint3d startPoint = new OdGePoint3d();
      pArc.getStartPoint(startPoint);
      pDimension.setXLine1Point(startPoint);

      OdGePoint3d endPoint = new OdGePoint3d();
      pArc.getEndPoint(endPoint);
      pDimension.setXLine2Point(endPoint);
    }
    /************************************************************************/
    /* Add a Text with Field to the specified BlockTableRecord              */
    /************************************************************************/
    void addTextWithField(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId,
                            OdDbObjectId noteStyleId)
    {
      OdDbBlockTableRecord pRecord = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      //  double dx = w/16.0;
      //  double dy = h/12.0;

      OdGePoint3d textPos1 = new OdGePoint3d(point);
      textPos1.setX(textPos1.getX() + w / 15.0);
      textPos1.setY(textPos1.getY() - h / 3.0);

      OdGePoint3d textPos2 = new OdGePoint3d(point);
      textPos2.setX(textPos2.getX() + w / 15.0);
      textPos2.setY(textPos2.getY() - (2.0 * h / 3.0));

      double textHeight = m_EntityBoxes.getHeight() / 12.0;

      /**********************************************************************/
      /* Prepare the text entities                                           */
      /**********************************************************************/
      OdDbText pText1 = OdDbText.createObject();
      OdDbObjectId textId = pRecord.appendOdDbEntity(pText1);
      OdDbText pText2 = OdDbText.createObject();
      OdDbObjectId textId2 = pRecord.appendOdDbEntity(pText2);

      pText1.setPosition(textPos1);
      pText1.setHeight(textHeight);
      pText2.setPosition(textPos2);
      pText2.setHeight(textHeight);
      if (!styleId.isNull())
      {
        pText1.setTextStyle(styleId);
        pText2.setTextStyle(styleId);
      }

      /**********************************************************************/
      /* Create field objects                                               */
      /**********************************************************************/
      OdDbField pTextField1 = OdDbField.createObject();
      OdDbField pField1_1 = OdDbField.createObject();

      OdDbField pTextField2 = OdDbField.createObject();
      OdDbField pField2_1 = OdDbField.createObject();
      OdDbField pField2_2 = OdDbField.createObject();

      /**********************************************************************/
      /* Set field objects                                                  */
      /**********************************************************************/
      OdDbObjectId textFldId1 = pText1.setField("TEXT", pTextField1);
      OdDbObjectId fldId1_1 = pTextField1.setField("", pField1_1);

      OdDbObjectId textFldId2 = pText2.setField("TEXT", pTextField2);

      /**********************************************************************/
      /* Set field property                                                 */
      /**********************************************************************/

      pField1_1.setEvaluationOption(OdDbField.EvalOption.kAutomatic);
      String fc1 = "\\AcVar Comments";
      pField1_1.setFieldCode(fc1);

      pTextField1.setEvaluationOption(OdDbField.EvalOption.kAutomatic);
      String fc2 = "%<\\_FldIdx 0>%";
      // string commented as it seems it should be unsafe enum, as |-d value does not belong to any of existing enum values
      //pTextField1.setFieldCode(fc2, OdDbField.FieldCodeFlag.swigToEnum(OdDbField.FieldCodeFlag.kTextField.swigValue() | OdDbField.FieldCodeFlag.kPreserveFields.swigValue()));
      pTextField1.setFieldCode(fc2, OdDbField.FieldCodeFlag.kTextField);

      /**********************************************************************/
      /* Evaluate field                                                     */
      /**********************************************************************/
      pField1_1.evaluate((int)OdDbField.EvalContext.kDemand.swigValue());

      OdDbFieldArray fldArray = new OdDbFieldArray();
      fldArray.add(pField2_1);
      fldArray.add(pField2_2);

      pTextField2.setEvaluationOption(OdDbField.EvalOption.kAutomatic);
      String fc3 = "Date %<\\_FldIdx 0>% Time %<\\_FldIdx 1>%";
      pTextField2.setFieldCode(fc3, OdDbField.FieldCodeFlag.kTextField, fldArray);

      pField2_1.setEvaluationOption(OdDbField.EvalOption.kAutomatic);
      String fc4 = "\\AcVar Date \\f M/dd/yyyy";
      pField2_1.setFieldCode(fc4);

      pField2_2.setEvaluationOption(OdDbField.EvalOption.kAutomatic);
      String fc5 = "\\AcVar Date \\f h:mm tt";
      pField2_2.setFieldCode(fc5);

      /**********************************************************************/
      /* Evaluate fields                                                    */
      /**********************************************************************/
      pField2_1.evaluate((int)OdDbField.EvalContext.kDemand.swigValue());
      pField2_2.evaluate((int)OdDbField.EvalContext.kDemand.swigValue());

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(pRecord,
        point.add(m_textOffset), point.add(m_textOffset),
        "FIELDS", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, noteStyleId);
    }
    /************************************************************************/
    /* Add a Sphere to the specified BlockTableRecord                       */
    /************************************************************************/
    void addSphere(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //double h    = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Sphere", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
      p3dSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(p3dSolid);

      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      p3dSolid.createSphere(w * 3.0 / 8.0);
      p3dSolid.transformBy(xfm);
    }

    /************************************************************************/
    /* Add a text entity with the specified attributes to the specified     */
    /* BlockTableRecord                                                     */
    /************************************************************************/
    OdDbObjectId addTextEnt(OdDbBlockTableRecord bBTR, OdGePoint3d position, OdGePoint3d ap,
       String str, double height, TextHorzMode hMode, TextVertMode vMode, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      return addTextEnt(bBTR, position, ap, str, height, hMode, vMode, layerId, styleId, null);
    }

    OdDbObjectId addTextEnt(OdDbBlockTableRecord bBTR, OdGePoint3d position, OdGePoint3d ap, String str, double height, TextHorzMode hMode, TextVertMode vMode, OdDbObjectId layerId, OdDbObjectId styleId, OdDbGroup pGroup)
    {
      /**********************************************************************/
      /* Create the text object                                             */
      /**********************************************************************/
      OdDbText pText = OdDbText.createObject();
      pText.setDatabaseDefaults(bBTR.database());
      OdDbObjectId textId = bBTR.appendOdDbEntity(pText);

      // Make the text annotative
      OdDbAnnotativeObjectPE.cast(pText).setAnnotative(pText, true);

      /**********************************************************************/
      /* Add the text to the specified group                                */
      /**********************************************************************/
      if (pGroup != null)
      {
        pGroup.append(textId);
      }

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pText.setPosition(position);
      pText.setAlignmentPoint(ap);
      pText.setHeight(height);
      pText.setWidthFactor(1.0);
      pText.setTextString(str);
      pText.setHorizontalMode(hMode);
      pText.setVerticalMode(vMode);

      /**********************************************************************/
      /* Set the text to the specified style                                */
      /**********************************************************************/
      if (!styleId.isNull())
      {
        pText.setTextStyle(styleId);
      }
      /**********************************************************************/
      /* Set the text to the specified layer                                */
      /**********************************************************************/
      if (!layerId.isNull())
      {
        pText.setLayer(layerId, false);
      }

      return textId;
    }
    
    /************************************************************************/
    /* Add a Block Definition to the specified database                     */
    /************************************************************************/
    OdDbObjectId addBlockDef(OdDbDatabase pDb, String name, int boxRow, int boxCol)
    {
      /**********************************************************************/
      /* Open the block table                                               */
      /**********************************************************************/
      OdDbBlockTable pBlocks = OdDbBlockTable.cast(pDb.getBlockTableId().safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create a BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.createObject();

      /**********************************************************************/
      /* Block must have a name before adding it to the table.              */
      /**********************************************************************/
      bBTR.setName(name);

      /**********************************************************************/
      /* Add the record to the table.                                       */
      /**********************************************************************/
      OdDbObjectId btrId = pBlocks.add(bBTR);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add a Circle                                                       */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pCircle);

      OdGePoint3d center = new OdGePoint3d(0, 0, 0);
      center.setX(center.getX() - (w * 2.5 / 8.0));

      pCircle.setCenter(center);
      pCircle.setRadius(w * 1.0 / 8.0);

      /**********************************************************************/
      /* Add an Arc                                                         */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pArc);

      pArc.setRadius(w * 1.0 / 8.0);

      center = new OdGePoint3d(0, 0, 0);
      center.setY(center.getY() - (pArc.radius() / 2.0));

      pArc.setCenter(center);
      pArc.setStartAngle(OdaToRadian(0.0));
      pArc.setEndAngle(OdaToRadian(180.0));

      /**********************************************************************/
      /* Add an Ellipse                                                     */
      /**********************************************************************/
      OdDbEllipse pEllipse = OdDbEllipse.createObject();
      pEllipse.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pEllipse);

      center = new OdGePoint3d(0, 0, 0);
      center.setX(center.getX() + (w * 2.5 / 8.0));

      double majorRadius = w * 1.0 / 8.0;
      OdGeVector3d majorAxis = new OdGeVector3d(majorRadius, 0.0, 0.0);
      majorAxis.rotateBy(OdaToRadian(30.0), OdGeVector3d.getKZAxis());

      double radiusRatio = 0.25;

      pEllipse.set(center, OdGeVector3d.getKZAxis(), majorAxis, radiusRatio);

      /**********************************************************************/
      /* Add an Attdef                                                      */
      /**********************************************************************/
      OdDbAttributeDefinition pAttDef = OdDbAttributeDefinition.createObject();
      pAttDef.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pAttDef);

      pAttDef.setPrompt("Enter ODT_ATT: ");
      pAttDef.setTag("Oda_ATT");
      pAttDef.setHorizontalMode(TextHorzMode.kTextCenter);
      pAttDef.setHeight(0.1);
      pAttDef.setTextString("Default");

      /**********************************************************************/
      /* Return the ObjectId of the BlockTableRecord                        */
      /**********************************************************************/
      return btrId;
    }

    /************************************************************************/
    /* Add a dimension style to the specified database                      */
    /************************************************************************/
    OdDbObjectId addDimStyle(OdDbDatabase pDb,
                                       String dimStyleName)
    {
      /**********************************************************************/
      /* Create the DimStyle                                                */
      /**********************************************************************/
      OdDbDimStyleTableRecord pDimStyle = OdDbDimStyleTableRecord.createObject();
      /**********************************************************************/
      /* Set the name                                                       */
      /**********************************************************************/
      pDimStyle.setName(dimStyleName);
      /**********************************************************************/
      /* Open the DimStyleTable                                             */
      /**********************************************************************/
      OdDbDimStyleTable pTable = OdDbDimStyleTable.cast(pDb.getDimStyleTableId().safeOpenObject(OpenMode.kForWrite));
      /**********************************************************************/
      /* Add the DimStyle                                                   */
      /**********************************************************************/
      OdDbObjectId dimStyleId = pTable.add(pDimStyle);
      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pDimStyle.setDimtxsty(new OdDbHardPointerId(pDb.getTextStyleStandardId()));
      pDimStyle.setDimsah(true);
      pDimStyle.setDimblk1("_OBLIQUE");
      pDimStyle.setDimblk2("_DOT");
      return dimStyleId;
    }

    /************************************************************************/
    /* Add an MLine Style to the specified database                         */
    /************************************************************************/
    OdDbObjectId addMLineStyle(OdDbDatabase pDb, String name, String desc)
    {
      /**********************************************************************/
      /* Open the MLineStyle dictionary                                     */
      /**********************************************************************/
      OdDbDictionary pMLDic = OdDbDictionary.cast(pDb.getMLStyleDictionaryId().safeOpenObject(OpenMode.kForWrite));
      //OdDbDictionary pMLDic = new OdDbDictionary(OdDbObject.getCPtr(pDb.getMLStyleDictionaryId().safeOpenObject(OpenMode.kForWrite)), false);

      /**********************************************************************/
      /* Create an Mline Style                                              */
      /**********************************************************************/
      OdDbMlineStyle pStyle = OdDbMlineStyle.createObject();
      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pStyle.setName(name);
      pStyle.setDescription(desc);
      pStyle.setStartAngle(OdaToRadian(105.0));
      pStyle.setEndAngle(OdaToRadian(75.0));
      pStyle.setShowMiters(true);
      pStyle.setStartSquareCap(true);
      pStyle.setEndSquareCap(true);

      /**********************************************************************/
      /* Get the object ID of the desired linetype                          */
      /**********************************************************************/
      OdDbLinetypeTable pLtTable = OdDbLinetypeTable.cast(pDb.getLinetypeTableId().safeOpenObject());
      OdDbObjectId linetypeId = pLtTable.getAt("Hidden");

      OdCmColor color = new OdCmColor();

      /**********************************************************************/
      /* Add some elements                                                  */
      /**********************************************************************/
      color.setRGB((byte)255, (byte)0, (byte)0);
      pStyle.addElement(0.1, color, linetypeId);
      color.setRGB((byte)0, (byte)255, (byte)0);
      pStyle.addElement(0.0, color, linetypeId);

      /**********************************************************************/
      /* Update the MLine dictionary                                        */
      /**********************************************************************/
      return pMLDic.setAt(name, pStyle);
    }

    /************************************************************************/
    /* Add a Material to the specified database                             */
    /************************************************************************/
    OdDbObjectId addMaterial(OdDbDatabase pDb, String name, String desc)
    {
      /**********************************************************************/
      /* Open the Material dictionary                                     */
      /**********************************************************************/
      OdDbDictionary pMatDic = OdDbDictionary.cast(pDb.getMaterialDictionaryId().safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create a Material                                                  */
      /**********************************************************************/
      OdDbMaterial pMaterial = OdDbMaterial.createObject();
      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pMaterial.setName(name);
      pMaterial.setDescription(desc);

      OdGiMaterialColor materialColor = new OdGiMaterialColor();
      materialColor.setMethod(OdGiMaterialColor.Method.kOverride);
      materialColor.setFactor(0.75);
      materialColor.setColor(new OdCmEntityColor((byte)192, (byte)32, (byte)255));

      OdGiMaterialMap materialMap = new OdGiMaterialMap();
      materialMap.setBlendFactor(0.0);
      materialMap.setSource(OdGiMaterialMap.Source.kFile);

      pMaterial.setAmbient(materialColor);
      pMaterial.setBump(materialMap);
      pMaterial.setDiffuse(materialColor, materialMap);
      pMaterial.setOpacity(1.0, materialMap);
      pMaterial.setReflection(materialMap);
      pMaterial.setRefraction(1.0, materialMap);
      pMaterial.setTranslucence(0.0);
      pMaterial.setSelfIllumination(0.0);
      pMaterial.setReflectivity(0.0);
      pMaterial.setMode(OdGiMaterialTraits.Mode.kRealistic);
      pMaterial.setChannelFlags(OdGiMaterialTraits.ChannelFlags.kNone);
      pMaterial.setIlluminationModel(OdGiMaterialTraits.IlluminationModel.kBlinnShader);

      materialColor.setFactor(1.0);
      materialColor.setColor(new OdCmEntityColor((byte)255, (byte)255, (byte)255));
      pMaterial.setSpecular(materialColor, materialMap, 0.67);
      /**********************************************************************/
      /* Update the Material dictionary                                        */
      /**********************************************************************/
      return pMatDic.setAt(name, pMaterial);
    }
    /************************************************************************/
    /* Add a PaperSpace viewport to the specified database                  */
    /************************************************************************/
    void addPsViewport(OdDbDatabase pDb, OdDbObjectId layerId)
    {
      /**********************************************************************/
      /* Enable PaperSpace                                                  */
      /*                                                                    */
      /* NOTE: This is required to cause Teigha to automatically create     */
      /* the overall viewport. If not called before opening PaperSpace      */
      /* BlockTableRecord,   the first viewport created IS the the overall  */
      /* viewport.                                                          */
      /**********************************************************************/
      pDb.setTILEMODE(false);

      /**********************************************************************/
      /* Open PaperSpace                                                    */
      /**********************************************************************/
      OdDbBlockTableRecord pPs = OdDbBlockTableRecord.cast(pDb.getPaperSpaceId().safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Disable PaperSpace                                                 */
      /**********************************************************************/
      // pDb.setTILEMODE(1);

      /**********************************************************************/
      /* Create the viewport                                                */
      /**********************************************************************/
      OdDbViewport pVp = OdDbViewport.createObject();
      pVp.setDatabaseDefaults(pDb);
      /**********************************************************************/
      /* Add it to PaperSpace                                               */
      /**********************************************************************/
      pPs.appendOdDbEntity(pVp);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pVp.setCenterPoint(new OdGePoint3d(5.25, 4.0, 0));
      pVp.setWidth(10.0);
      pVp.setHeight(7.5);
      pVp.setViewTarget(new OdGePoint3d(0, 0, 0));
      pVp.setViewDirection(new OdGeVector3d(0, 0, 1));
      pVp.setViewHeight(8.0);
      pVp.setLensLength(50.0);
      pVp.setViewCenter(new OdGePoint2d(5.25, 4.0));
      pVp.setSnapIncrement(new OdGeVector2d(0.25, 0.25));
      pVp.setGridIncrement(new OdGeVector2d(0.25, 0.25));
      pVp.setCircleSides((int)(20000));

      /**********************************************************************/
      /* Freeze a layer in this viewport                                    */
      /**********************************************************************/
      OdDbObjectIdArray frozenLayers = new OdDbObjectIdArray();
      frozenLayers.add(layerId);
      pVp.freezeLayersInViewport(frozenLayers);

      /**********************************************************************/
      /* Add a circle to this PaperSpace Layout                             */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(pDb);
      pPs.appendOdDbEntity(pCircle);
      pCircle.setRadius(1.0);
      pCircle.setCenter(new OdGePoint3d(1.0, 1.0, 0.0));
      pCircle.setLayer(layerId, false);

      /**********************************************************************/
      /* Disable PaperSpace                                                 */
      /**********************************************************************/
      pDb.setTILEMODE(true);
    }
    /************************************************************************/
    /* Add entity boxes to specified BlockTableRecord                       */
    /************************************************************************/
    void createEntityBoxes(OdDbObjectId btrId, OdDbObjectId layerId)
    {
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      OdGePoint3d currentPoint;

      /**********************************************************************/
      /* Create a 2D polyline for each box                                  */
      /**********************************************************************/
      for (int j = 0; j < EntityBoxes.VER_BOXES; j++)
      {
        for (int i = 0; i < EntityBoxes.HOR_BOXES; i++)
        {
          if (!m_EntityBoxes.isBox(j, i))
            break;

          double wCurBox = m_EntityBoxes.getWidth(j, i);
          currentPoint = m_EntityBoxes.getBox(j, i);

          OdDb2dPolyline pPline = OdDb2dPolyline.createObject();
          pPline.setDatabaseDefaults(pDb);

          bBTR.appendOdDbEntity(pPline);

          OdDb2dVertex pVertex;

          pVertex = OdDb2dVertex.createObject();
          pVertex.setDatabaseDefaults(pDb);
          pPline.appendVertex(pVertex);
          OdGePoint3d pos = currentPoint;
          pVertex.setPosition(pos);

          pVertex = OdDb2dVertex.createObject();
          pPline.appendVertex(pVertex);
          pos.setX(pos.getX() + wCurBox);
          pVertex.setPosition(pos);

          pVertex = OdDb2dVertex.createObject();
          pPline.appendVertex(pVertex);
          pos.setY(pos.getY() - m_EntityBoxes.getHeight());
          pVertex.setPosition(pos);

          pVertex = OdDb2dVertex.createObject();
          pPline.appendVertex(pVertex);
          pos.setX(pos.getX() - wCurBox);
          pVertex.setPosition(pos);


          pPline.makeClosed();

          pPline.setColorIndex(OdCmEntityColor.ACIcolorMethod.kACIBlue.swigValue(), true);
          pPline.setLayer(layerId, true);
        }
      }
      /**********************************************************************/
      /* 'Zoom' the box array by resizing the active tiled MS viewport      */
      /**********************************************************************/
      OdDbViewportTable pVpTable = OdDbViewportTable.cast(pDb.getViewportTableId().safeOpenObject(OpenMode.kForWrite));
      OdDbObjectId vpID = pVpTable.getActiveViewportId();
      OdDbViewportTableRecord vPortRec = OdDbViewportTableRecord.cast(vpID.safeOpenObject(OpenMode.kForWrite));

      OdGePoint3d center = m_EntityBoxes.getArrayCenter();
      vPortRec.setCenterPoint(center.convert2d());

      OdGeVector3d size = m_EntityBoxes.getArraySize();
      vPortRec.setHeight(1.05 * Math.abs(size.getY()));
      vPortRec.setWidth(1.05 * Math.abs(size.getX()));
      vPortRec.setCircleSides(20000);
    }
    /************************************************************************/
    /* Add a 2D (heavy) polyline to the specified BlockTableRecord          */
    /************************************************************************/
    void add2dPolyline(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "2D POLYLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add a 2dPolyline to the database                                   */
      /**********************************************************************/
      OdDb2dPolyline pPline = OdDb2dPolyline.createObject();
      pPline.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pPline);

      /**********************************************************************/
      /* Add the vertices                                                   */
      /**********************************************************************/
      OdGePoint3d pos = point;
      pos.setY(pos.getY() - h);
      pos.setX(pos.getX() + (w / 8));
      pos.setY(pos.getY() + (h / 8));

      double[][] width = 
      {
        {0.0, w/12, w/4, 0.0},
        {w/4, w/12, 0.0, 0.0}
      };

      for (int i = 0; i < 4; i++)
      {
        OdDb2dVertex pVertex = OdDb2dVertex.createObject();
        pVertex.setDatabaseDefaults(bBTR.database());
        pPline.appendVertex(pVertex);
        pVertex.setPosition(pos);
        pos.setX(pos.getX() + (w / 4.0));
        pos.setY(pos.getY() + (h / 4.0));
        pVertex.setStartWidth(width[0][i]);
        pVertex.setEndWidth(width[1][i]);
      }
    }

    /************************************************************************/
    /* Add a 3D polyline to the specified BlockTableRecord                  */
    /************************************************************************/
    void add3dPolyline(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "3D POLYLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add a 3dPolyline to the database                                   */
      /**********************************************************************/
      OdDb3dPolyline pPline = OdDb3dPolyline.createObject();
      pPline.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pPline);

      /**********************************************************************/
      /* Add the vertices                                                   */
      /**********************************************************************/
      OdGePoint3d pos = point;
      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);

      double radius = w * 3.0 / 8.0;
      double height = 0.0;
      double theta = 0.0;

      int turns = 4;
      int segs = 16;
      int points = segs * turns;

      double deltaR = radius / points;
      double deltaTheta = Math.PI * 2 / segs;
      double deltaH = 2 * radius / points;

      OdGeVector3d vec = new OdGeVector3d(radius, 0, 0);

      for (int i = 0; i < points; i++)
      {
        OdDb3dPolylineVertex pVertex = OdDb3dPolylineVertex.createObject();
        pVertex.setDatabaseDefaults(bBTR.database());
        pPline.appendVertex(pVertex);
        pVertex.setPosition(center.add(vec));

        radius -= deltaR;
        height += deltaH;
        theta += deltaTheta;
        vec = new OdGeVector3d(radius, 0, height).rotateBy(theta, OdGeVector3d.getKZAxis());
      }
    }

    /************************************************************************/
    /* Append a PolyFaceMesh vertex to the specified PolyFaceMesh           */
    /************************************************************************/
    void appendPfMeshVertex(OdDbPolyFaceMesh pMesh, double x, double y, double z)
    {
      /**********************************************************************/
      /* Append a MeshVertex to the PolyFaceMesh                            */
      /**********************************************************************/
      OdDbPolyFaceMeshVertex pVertex = OdDbPolyFaceMeshVertex.createObject();
      pMesh.appendVertex(pVertex);

      /**********************************************************************/
      /* Set the properties                                                 */
      /**********************************************************************/
      pVertex.setPosition(new OdGePoint3d(x, y, z));
    }

    /************************************************************************/
    /* Append a FaceRecord to the specified PolyFaceMesh                    */
    /************************************************************************/
    void appendFaceRecord(OdDbPolyFaceMesh pMesh, short i1, short i2, short i3, short i4)
    {
      /**********************************************************************/
      /* Append a FaceRecord to the PolyFaceMesh                            */
      /**********************************************************************/
      OdDbFaceRecord pFr = OdDbFaceRecord.createObject();
      pMesh.appendFaceRecord(pFr);

      /**********************************************************************/
      /* Set the properties                                                 */
      /**********************************************************************/
      pFr.setVertexAt(0, i1);
      pFr.setVertexAt(1, i2);
      pFr.setVertexAt(2, i3);
      pFr.setVertexAt(3, i4);
    }
    public static double OdaToRadian(double deg) { return (deg) * Math.PI / 180.0; }

    /************************************************************************/
    /* Add a PolyFaceMesh to the specified BlockTableRecord                 */
    /************************************************************************/
    void addPolyFaceMesh(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "PolyFaceMesh", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add a PolyFaceMesh to the database                                 */
      /**********************************************************************/
      OdDbPolyFaceMesh pMesh = OdDbPolyFaceMesh.createObject();
      pMesh.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pMesh);


      /**********************************************************************/
      /* Add the faces and vertices that define a pup tent                  */
      /**********************************************************************/

      double dx = w * 3.0 / 8.0;
      double dy = h * 3.0 / 8.0;
      double dz = dy;

      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);

      appendPfMeshVertex(pMesh, center.getX() + dx, center.getY() + dy, 0);
      appendPfMeshVertex(pMesh, center.getX() + 0, center.getY() + dy, center.getZ() + dz);
      appendPfMeshVertex(pMesh, center.getX() - dx, center.getY() + dy, 0);
      appendPfMeshVertex(pMesh, center.getX() - dx, center.getY() - dy, 0);
      appendPfMeshVertex(pMesh, center.getX() + 0, center.getY() - dy, center.getZ() + dz);
      appendPfMeshVertex(pMesh, center.getX() + dx, center.getY() - dy, 0);

      appendFaceRecord(pMesh, (short)1, (short)2, (short)5, (short)6);
      appendFaceRecord(pMesh, (short)2, (short)3, (short)4, (short)5);
      appendFaceRecord(pMesh, (short)6, (short)5, (short)4, (short)0);
      appendFaceRecord(pMesh, (short)3, (short)2, (short)1, (short)0);
    }

    /************************************************************************/
    /* Add PolygonMesh to the specified BlockTableRecord                    */
    /************************************************************************/
    void addPolygonMesh(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "PolygonMesh", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add a PolygonMesh to the database                                 */
      /**********************************************************************/
      OdDbPolygonMesh pMesh = OdDbPolygonMesh.createObject();
      pMesh.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pMesh);

      /**********************************************************************/
      /* Define the size of the mesh                                        */
      /**********************************************************************/
      short mSize = 16, nSize = 4;
      pMesh.setMSize(mSize);
      pMesh.setNSize(nSize);


      /**********************************************************************/
      /* Define a profile                                                   */
      /**********************************************************************/
      double dx = w * 3.0 / 8.0;
      double dy = h * 3.0 / 8.0;

      OdGeVector3dArray vectors = new OdGeVector3dArray();
      vectors.add(new OdGeVector3d(0, -dy, 0));
      vectors.add(new OdGeVector3d(dx, -dy, 0));
      vectors.add(new OdGeVector3d(dx, dy, 0));
      vectors.add(new OdGeVector3d(0, dy, 0));

      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);

      /**********************************************************************/
      /* Append the vertices to the mesh                                    */
      /**********************************************************************/
      for (int i = 0; i < mSize; i++)
      {
        for (int j = 0; j < nSize; j++)
        {
          appendPgMeshVertex(pMesh, center.add(vectors.get(j)));
          vectors.get(j).rotateBy(OdaToRadian(360.0 / mSize), OdGeVector3d.getKYAxis());
        }
      }
      pMesh.makeMClosed();
    }

    /************************************************************************/
    /* Add some curves to the specified BlockTableRecord                    */
    /************************************************************************/
    void addCurves(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Create a Circle                                                    */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pCircle);

      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      center.setX(center.getX() - (w * 2.5 / 8.0));
      pCircle.setCenter(center);
      pCircle.setRadius(w * 1.0 / 8.0);

      /**********************************************************************/
      /* Add a Hyperlink to the Circle                                      */
      /**********************************************************************/
      OdDbEntityHyperlinkPE hpe = OdDbEntityHyperlinkPE.cast(pCircle);
      OdDbHyperlinkCollection urls = hpe.getHyperlinkCollection(pCircle);

      urls.addTail("http://forum.opendesign.com/forumdisplay.php?s=&forumid=17",
        "Open Design Alliance Forum > Teigha, C++ version");

      hpe.setHyperlinkCollection(pCircle, urls);

      if (!hpe.hasHyperlink(pCircle))
      {
        //throw new OdError("Hyperlinks are broken");
        System.out.println("Hyperlinks are broken\n");
      }
      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      double textY = point.getY() - (m_textSize / 2.0);

      addTextEnt(bBTR,
        new OdGePoint3d(center.getX(), textY, 0), new OdGePoint3d(center.getX(), textY, 0),
        "CIRCLE", m_textSize, TextHorzMode.kTextCenter, TextVertMode.kTextTop, layerId, styleId);
      addTextEnt(bBTR,
        new OdGePoint3d(center.getX(), textY - 1.6 * m_textSize, 0), new OdGePoint3d(center.getX(), textY - 1.6 * m_textSize, 0),
        "w/Hyperlink", m_textSize, TextHorzMode.kTextCenter, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create an Arc                                                      */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pArc);

      pArc.setRadius(w * 1.0 / 8.0);

      center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);

      center.setY(center.getY() + (pArc.radius() / 2.0));

      pArc.setCenter(center);
      pArc.setStartAngle(OdaToRadian(0.0));
      pArc.setEndAngle(OdaToRadian(180.0));

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR,
        new OdGePoint3d(center.getX(), textY, 0), new OdGePoint3d(center.getX(), textY, 0),
        "ARC", m_textSize, TextHorzMode.kTextCenter, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add an Ellipse                                                     */
      /**********************************************************************/
      OdDbEllipse pEllipse = OdDbEllipse.createObject();
      pEllipse.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pEllipse);

      double majorRadius = w * 1.0 / 8.0;
      double radiusRatio = 0.25;

      center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      center.setX(center.getX() + (w * 2.5 / 8.0));
      center.setY(center.getY() + majorRadius);

      OdGeVector3d majorAxis = new OdGeVector3d(majorRadius, 0.0, 0.0);
      majorAxis.rotateBy(OdaToRadian(30.0), OdGeVector3d.getKZAxis());

      pEllipse.set(center, OdGeVector3d.getKZAxis(), majorAxis, radiusRatio);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR,
        new OdGePoint3d(center.getX(), textY, 0), new OdGePoint3d(center.getX(), textY, 0),

        "ELLIPSE", m_textSize, TextHorzMode.kTextCenter, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add a Point                                                        */
      /**********************************************************************/
      OdDbPoint pPoint = OdDbPoint.createObject();
      pPoint.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pPoint);

      center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      center.setY(center.getY() - ((h * 1.0) / 8.0));

      pPoint.setPosition(center);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      center.setY(center.getY() + ((h * 1.0) / 8.0));
      addTextEnt(bBTR, center, center,
        "POINT", m_textSize, TextHorzMode.kTextCenter, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Set the point display mode so we can see it                        */
      /**********************************************************************/
      pDb.database().setPDMODE((short)3);
      pDb.database().setPDSIZE(0.1);
    }

    /************************************************************************/
    /* Add a tolerance to the specified BlockTableRecord                    */
    /************************************************************************/
    void addTolerance(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "TOLERANCE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);


      /**********************************************************************/
      /* Add a Frame Control Feature (Tolerance) to the database            */
      /**********************************************************************/
      OdDbFcf pTol = OdDbFcf.createObject();
      pTol.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pTol);

      /**********************************************************************/
      /* Set the properties                                                 */
      /**********************************************************************/
      pTol.setDatabaseDefaults(pDb);
      point.setX(point.getX() + (w / 6.0));
      point.setY(point.getY() - (h / 4.0));
      pTol.setLocation(point);
      pTol.setText("{\\Fgdt;r}%%v{\\Fgdt;n}3.2{\\Fgdt;m}%%v%%v%%v%%v");
    }

    /************************************************************************/
    /* Add some leaders the specified BlockTableRecord                      */
    /************************************************************************/
    void addLeaders(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {

      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "LEADERs", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Define an annotation block -- A circle with radius 0.5             */
      /**********************************************************************/
      OdDbBlockTable pBlocks = OdDbBlockTable.cast(pDb.getBlockTableId().safeOpenObject(OpenMode.kForWrite));
      OdDbBlockTableRecord pAnnoBlock = OdDbBlockTableRecord.createObject();
      pAnnoBlock.setName("AnnoBlock");
      OdDbObjectId annoBlockId = pBlocks.add(pAnnoBlock);
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());
      pAnnoBlock.appendOdDbEntity(pCircle);
      OdGePoint3d center = new OdGePoint3d(0.5, 0, 0);
      pCircle.setCenter(center);
      pCircle.setRadius(0.5);

      /**********************************************************************/
      /* Add a leader with database defaults to the database                */
      /**********************************************************************/
      OdDbLeader pLeader = OdDbLeader.createObject();
      pLeader.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pLeader);
      /**********************************************************************/
      /* Add the vertices                                                   */
      /**********************************************************************/
      point.setX(point.getX() + (w * 1.0 / 8.0));
      point.setY(point.getY() - ((h * 3.0) / 8.0));
      pLeader.appendVertex(point);
      point.setX(point.getX() + (w * 2.0 / 8.0));
      point.setX(point.getX() + (h * 1.0 / 8.0));
      pLeader.appendVertex(point);

      /**********************************************************************/
      /* Insert the annotation                                              */
      /**********************************************************************/
      OdDbBlockReference pBlkRef = OdDbBlockReference.createObject();
      OdDbObjectId blkRefId = bBTR.appendOdDbEntity(pBlkRef);
      pBlkRef.setBlockTableRecord(annoBlockId);
      pBlkRef.setPosition(point);
      pBlkRef.setScaleFactors(new OdGeScale3d(0.375, 0.375, 0.375));

      /**********************************************************************/
      /* Attach the Block Reference as annotation to the Leader             */
      /**********************************************************************/
      pLeader.attachAnnotation(blkRefId);

      /**********************************************************************/
      /* Add a leader with database defaults to the database                */
      /**********************************************************************/
      pLeader = OdDbLeader.createObject();
      pLeader.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pLeader);

      /**********************************************************************/
      /* Add the vertices                                                   */
      /**********************************************************************/
      point = m_EntityBoxes.getBox(boxRow, boxCol);
      point.setX(point.getX() + (w * 1.0 / 8.0));
      point.setY(point.getY() - (h * 5.0 / 8.0));
      pLeader.appendVertex(point);
      point.setX(point.getX() + (w * 1.0 / 8.0));
      point.setY(point.getY() + (h * 1.0 / 8.0));
      pLeader.appendVertex(point);
      point.setX(point.getX() + (w * 1.0 / 8));

      /**********************************************************************/
      /* Set the arrowhead                                                  */
      /**********************************************************************/
      pLeader.setDimldrblk("DOT");

      /**********************************************************************/
      /* Create MText at a 30 angle                                        */
      /**********************************************************************/
      OdDbMText pMText = OdDbMText.createObject();
      pMText.setDatabaseDefaults(pDb);
      OdDbObjectId mTextId = bBTR.appendOdDbEntity(pMText);
      double textHeight = 0.15;
      double textWidth = 1.0;
      pMText.setLocation(point);
      pMText.setRotation(OdaToRadian(10.0));
      pMText.setTextHeight(textHeight);
      pMText.setWidth(textWidth);
      pMText.setAttachment(OdDbMText.AttachmentPoint.kMiddleLeft);
      pMText.setContents("MText");
      pMText.setTextStyle(styleId);

      /**********************************************************************/
      /* Set a background color                                             */
      /**********************************************************************/
      OdCmColor cBackground = new OdCmColor();
      cBackground.setRGB((byte)255, (byte)255, (byte)0); // Yellow
      pMText.setBackgroundFillColor(cBackground);
      pMText.setBackgroundFill(true);
      pMText.setBackgroundScaleFactor(2.0);

      /**********************************************************************/
      /* Attach the MText as annotation to the Leader                       */
      /**********************************************************************/
      pLeader.attachAnnotation(mTextId);

      /**********************************************************************/
      /* Add a leader with database defaults to the database                */
      /**********************************************************************/
      pLeader = OdDbLeader.createObject();
      bBTR.appendOdDbEntity(pLeader);
      pLeader.setDatabaseDefaults(pDb);


      /**********************************************************************/
      /* Add the vertices                                                   */
      /**********************************************************************/
      point = m_EntityBoxes.getBox(boxRow, boxCol);
      point.setX(point.getX() + (w * 1.0 / 8.0));
      point.setY(point.getY() - (h * 7.0 / 8.0));
      pLeader.appendVertex(point);
      point.setX(point.getX() + (w * 1.0 / 8.0));
      point.setY(point.getY() + (h * 1.0 / 8.0));
      pLeader.appendVertex(point);

      /**********************************************************************/
      /* Create a Frame Control Feature (Tolerance)                         */
      /**********************************************************************/
      OdDbFcf pTol = OdDbFcf.createObject();
      pTol.setDatabaseDefaults(pDb);
      pTol.setLocation(point);
      pTol.setText("{\\Fgdt;r}%%v{\\Fgdt;n}3.2{\\Fgdt;m}%%v%%v%%v%%v");

      /**********************************************************************/
      /* Attach the FCF as annotation to the Leader                         */
      /**********************************************************************/
      pLeader.attachAnnotation(bBTR.appendOdDbEntity(pTol));
    }

    /************************************************************************/
    /* Add an Aligned Dimension to the specified BlockTableRecord           */
    /************************************************************************/
    void addAlignedDimension(OdDbObjectId btrId,
                                       int boxRow,
                                       int boxCol,
                                       OdDbObjectId layerId,
                                       OdDbObjectId styleId,
                                       OdDbObjectId dimStyleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Aligned", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a line to be dimensioned                                    */
      /**********************************************************************/
      OdGePoint3d line1Pt = new OdGePoint3d();
      OdGePoint3d line2Pt = new OdGePoint3d();
      line1Pt.setX(point.getX() + (w * 0.5 / 8.0));
      line1Pt.setY(point.getY() + (h * 1.5 / 8.0));
      line2Pt = line1Pt.add(new OdGeVector3d(1.5, 2.0, 0.0));

      OdDbLine pLine = OdDbLine.createObject();
      pLine.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pLine);
      pLine.setStartPoint(line1Pt);
      pLine.setEndPoint(line2Pt);

      /**********************************************************************/
      /* Create an aligned dimension and dimension the ends of the line     */
      /**********************************************************************/
      OdDbAlignedDimension pDimension = OdDbAlignedDimension.createObject();
      pDimension.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pDimension);

      OdGePoint3d dimLinePt = new OdGePoint3d();
      dimLinePt.setX(point.getX() + (w * 3.5 / 8.0));
      dimLinePt.setY(point.getY() + (h * 2.0 / 8.0));

      pDimension.setDimensionStyle(dimStyleId);
      pDimension.setXLine1Point(pLine.startPoint());
      pDimension.setXLine2Point(pLine.endPoint());
      pDimension.setDimLinePoint(dimLinePt);
      pDimension.useDefaultTextPosition();
      pDimension.setJogSymbolHeight(1.5);
    }

    /************************************************************************/
    /* Add an Mline to the specified BlockTableRecord                       */
    /************************************************************************/
    void addMLine(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "MLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of MLine                                 */
      /**********************************************************************/
      point.setX(point.getX() + (w / 10.0));
      point.setY(point.getY() - (h / 2));

      /**********************************************************************/
      /* Create an MLine and add it to the database                         */
      /**********************************************************************/
      OdDbMline pMLine = OdDbMline.createObject();
      pMLine.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pMLine);

      /**********************************************************************/
      /* Open the MLineStyle dictionary, and set the style                  */
      /**********************************************************************/
      OdDbDictionary pMLDic = OdDbDictionary.cast(pDb.getMLStyleDictionaryId().safeOpenObject());
      pMLine.setStyle(pMLDic.getAt("OdaStandard"));

      /**********************************************************************/
      /* Add some segments                                                  */
      /**********************************************************************/
      point.setY(point.getY() - (h / 2.2));
      pMLine.appendSeg(point);

      point.setY(point.getY() + (h / 3.0));
      pMLine.appendSeg(point);

      point.setY(point.getY() + (h / 5.0));
      point.setX(point.getX() + (w / 4.0));
      pMLine.appendSeg(point);

      point.setX(point.getX() + (w / 4.0));
      pMLine.appendSeg(point);

      point.setY(point.getY() + (h / 3.0));
      pMLine.appendSeg(point);

      point.setX(point.getX() + (w / 3));
      pMLine.appendSeg(point);

      point.setY(point.getY() - (h / 2));
      pMLine.appendSeg(point);

      point.setX(point.getX() - (w / 4));
      point.setY(point.getY() - (h / 3));
      pMLine.appendSeg(point);
    }

///////////////////////////////////////////////////////////
/////////////// Big method adding start ///////////////////
///////////////////////////////////////////////////////////
    /************************************************************************/
    /* Add an Arc Dimension to the specified BlockTableRecord               */
    /************************************************************************/
    void addArcDimension(OdDbObjectId btrId,
                                   int boxRow,
                                   int boxCol,
                                   OdDbObjectId layerId,
                                   OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Arc", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create an arc to be dimensioned                                    */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pArc);
      OdGePoint3d center = point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0));
      pArc.setCenter(center);
      pArc.setStartAngle(OdaToRadian(0.0));
      pArc.setEndAngle(OdaToRadian(90.0));
      pArc.setRadius(4.0 / Math.PI);


      /**********************************************************************/
      /* Create an ArcDimension                                             */
      /**********************************************************************/
      OdDbArcDimension pDimension = OdDbArcDimension.createObject();
      pDimension.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pDimension.setCenterPoint(pArc.center());
      pDimension.setArcPoint(pArc.center().add(new OdGeVector3d(pArc.radius() + 0.45, 0.0, 0.0)));

      OdGePoint3d startPoint = new OdGePoint3d();
      pArc.getStartPoint(startPoint);
      pDimension.setXLine1Point(startPoint);

      OdGePoint3d endPoint = new OdGePoint3d();
      pArc.getEndPoint(endPoint);
      pDimension.setXLine2Point(endPoint);

      pDimension.setArcSymbolType((short)1);

    }
    /************************************************************************/
    /* Add MText to the specified BlockTableRecord                          */
    /************************************************************************/
    void addMText(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "MTEXT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Add MText to the database                                          */
      /**********************************************************************/
      OdDbMText pMText = OdDbMText.createObject();
      pMText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pMText);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pMText.setLocation(point.add(new OdGeVector3d(w / 8.0, -h * 2.0 / 8.0, 0)));
      pMText.setTextHeight(0.4);
      pMText.setAttachment(OdDbMText.AttachmentPoint.kTopLeft);
      pMText.setContents("Sample {\\C1;MTEXT} created by {\\C5;OdWriteEx}");
      pMText.setWidth(w * 6.0 / 8.0);
      pMText.setTextStyle(styleId);


      /**********************************************************************/
      /* Add annotation scales                                              */
      /**********************************************************************/
      OdDbAnnotativeObjectPE.cast(pMText).setAnnotative(pMText, true);
      OdDbObjectContextCollection contextCollection = bBTR.database().objectContextManager().contextCollection("ACDB_ANNOTATIONSCALES");
      // OdString cast is necessary to avoid incorrect char pointer interpretation as an OdDbObjectContextId (that is essentially void*)
      OdDbObjectContext scale = contextCollection.getContext("1:2");
      if (scale != null)
        OdDbObjectContextInterface.cast(pMText).addContext(pMText, scale);
    }
    /************************************************************************/
    /* Add a block reference to the specified BlockTableRecord              */
    /************************************************************************/
    OdDbObjectId addInsert(OdDbBlockTableRecord bBTR, OdDbObjectId btrId, double xscale, double yscale)
    {
      /**********************************************************************/
      /* Add the block reference to the BlockTableRecord                    */
      /**********************************************************************/
      OdDbBlockReference pBlkRef = OdDbBlockReference.createObject();
      pBlkRef.setDatabaseDefaults(bBTR.database());
      OdDbObjectId brefId = bBTR.appendOdDbEntity(pBlkRef);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pBlkRef.setBlockTableRecord(btrId);
      pBlkRef.setScaleFactors(new OdGeScale3d(xscale, yscale, 1.0));
      return brefId;
    }
    /************************************************************************/
    /* Add a Block Reference to the specified BlockTableRecord              */
    /************************************************************************/
    void addBlockRef(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId, OdDbObjectId insertId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double      w     = m_EntityBoxes.getWidth(boxRow, boxCol);
      //  double      h     = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "INSERT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Insert the Block                                                   */
      /**********************************************************************/
      OdDbObjectId bklRefId = addInsert(bBTR, insertId, 1.0, 1.0);

      /**********************************************************************/
      /* Open the insert                                                    */
      /**********************************************************************/
      OdDbBlockReference pBlkRef = OdDbBlockReference.cast(bklRefId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create a transformation matrix for the block and attributes        */
      /**********************************************************************/
      OdGePoint3d insPoint = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      OdGeMatrix3d blkXfm = new OdGeMatrix3d();
      blkXfm.setTranslation(insPoint.asVector());
      pBlkRef.transformBy(blkXfm);

      /**********************************************************************/
      /* Scan the block definition for non-constant attribute definitions   */
      /* and use them as templates for attributes                           */
      /**********************************************************************/
      OdDbBlockTableRecord pBlockDef = OdDbBlockTableRecord.cast(insertId.safeOpenObject());
      OdDbObjectIterator pIter = pBlockDef.newIterator();
      for (pIter.start(); !pIter.done(); pIter.step())
      {
        OdDbEntity pEntity = pIter.entity();
        OdDbAttributeDefinition pAttDef = OdDbAttributeDefinition.cast(pEntity);
        if (pAttDef != null && !pAttDef.isConstant())
        {
          OdDbAttribute pAtt = OdDbAttribute.createObject();
          pAtt.setDatabaseDefaults(bBTR.database());
          pBlkRef.appendAttribute(pAtt);
          pAtt.setPropertiesFrom(pAttDef, false);
          pAtt.setAlignmentPoint(pAttDef.alignmentPoint());
          pAtt.setHeight(pAttDef.height());
          pAtt.setHorizontalMode(pAttDef.horizontalMode());
          pAtt.setNormal(pAttDef.normal());
          pAtt.setOblique(pAttDef.oblique());
          pAtt.setPosition(pAttDef.position());
          pAtt.setRotation(pAttDef.rotation());
          pAtt.setTextString(pAttDef.textString());
          pAtt.setTextStyle(pAttDef.textStyle());
          pAtt.setWidthFactor(pAttDef.widthFactor());

          /******************************************************************/
          /* Specify a new value for the attribute                          */
          /******************************************************************/
          pAtt.setTextString("The Value");

          /******************************************************************/
          /* Transform it as the block was transformed                      */
          /******************************************************************/
          pAtt.transformBy(blkXfm);
        }
      }
    }
    /************************************************************************/
    /* Add a 3D Face to the specified BlockTableRecord                      */
    /************************************************************************/
    void add3dFace(OdDbObjectId btrId,
                             int boxRow,
                             int boxCol,
                             OdDbObjectId layerId,
                             OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "3DFACE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a 3D Face                                                   */
      /**********************************************************************/
      OdDbFace pFace = OdDbFace.createObject();
      pFace.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pFace);

      pFace.setVertexAt(0, point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0)));
      pFace.setVertexAt(1, point.add(new OdGeVector3d(w * 7.0 / 8.0, h * 1.0 / 8.0, 0.0)));
      pFace.setVertexAt(2, point.add(new OdGeVector3d(w * 7.0 / 8.0, h * 6.0 / 8.0, 0.0)));
      pFace.setVertexAt(3, point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 6.0 / 8.0, 0.0)));
    }
    /************************************************************************/
    /* Add RText to the specified BlockTableRecord                          */
    /************************************************************************/
    void addRText(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double h    = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      point.addAssign(m_textOffset);
      addTextEnt(bBTR, point, point,
        "RTEXT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create RText DIESEL expression with no MText sequences             */
      /**********************************************************************/

      RText pRText = RText.createObject();
      pRText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pRText);

      point.addAssign(m_textLine.add(m_textLine));
      point.setX(point.getX() + (w / 16.0));
      pRText.setHeight(m_textSize);
      pRText.setPoint(point);
      pRText.setRotAngle(0.0);
      pRText.setToExpression(true);
      pRText.enableMTextSequences(false);
      pRText.setStringContents("Expression: 123{\\C5;456}");
      pRText.setTextStyle(styleId);

      /**********************************************************************/
      /* Create RText DIESEL expression with MText sequences                */
      /**********************************************************************/
      pRText = RText.createObject();
      pRText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pRText);

      point.addAssign(m_textLine);
      pRText.setHeight(m_textSize);
      pRText.setPoint(point);
      pRText.setRotAngle(0.0);
      pRText.setToExpression(true);
      pRText.enableMTextSequences(true);
      pRText.setStringContents("Expression: 123{\\C5;456}");
      pRText.setTextStyle(styleId);

      /**********************************************************************/
      /* Create RText External with no MText sequences                      */
      /**********************************************************************/
      pRText = RText.createObject();
      pRText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pRText);

      point.addAssign(m_textLine);
      pRText.setHeight(m_textSize);
      pRText.setPoint(point);
      pRText.setRotAngle(0.0);
      pRText.setToExpression(false);
      pRText.enableMTextSequences(false);
      pRText.setStringContents(inCurrentFolder("OdWriteEx.txt"));
      pRText.setTextStyle(styleId);

      /**********************************************************************/
      /* Create RText External with MText sequences                         */
      /**********************************************************************/
      pRText = RText.createObject();
      pRText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pRText);

      point.addAssign(m_textLine);
      pRText.setHeight(m_textSize);
      pRText.setPoint(point);
      pRText.setRotAngle(0.0);
      pRText.setToExpression(false);
      pRText.enableMTextSequences(true);
      pRText.setStringContents(inCurrentFolder("OdWriteEx.txt"));
      pRText.setTextStyle(styleId);

    }
    /************************************************************************/
    /* Add Hatches to the specified BlockTableRecord                          */
    /************************************************************************/
    void addHatches(OdDbObjectId btrId,
                              int boxRow,
                              int boxCol,
                              OdDbObjectId layerId,
                              OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();
      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double h    = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double delta = w / 12.0;

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "HATCHs", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create a rectangular Hatch with a circular hole                    */
      /**********************************************************************/
      OdDbHatch pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(pDb);
      OdDbObjectId whiteHatchId = bBTR.appendOdDbEntity(pHatch);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pHatch.setAssociative(false);
      pHatch.setPattern(OdDbHatch.HatchPatternType.kPreDefined, "SOLID");
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);

      /**********************************************************************/
      /* Define the outer loop with an OdGePolyline2d                       */
      /**********************************************************************/
      OdGePoint2dArray vertexPts = OdGePoint2dArray.repeat(OdGePoint2d.getKOrigin(), 4);
      OdGeDoubleArray vertexBulges = new OdGeDoubleArray();
      vertexPts.get(0).set(point.getX() + delta, point.getY() - delta);
      vertexPts.get(1).set(point.getX() + delta * 5, point.getY() - delta);
      vertexPts.get(2).set(point.getX() + delta * 5, point.getY() - delta * 5);
      vertexPts.get(3).set(point.getX() + delta, point.getY() - delta * 5);
      pHatch.appendLoop((int)(OdDbHatch.HatchLoopType.kExternal.swigValue() | OdDbHatch.HatchLoopType.kPolyline.swigValue()),
        vertexPts, vertexBulges);


      /**********************************************************************/
      /* Define an inner loop with an array of edges                        */
      /**********************************************************************/
      OdGePoint2d cenPt = new OdGePoint2d(point.getX() + delta * 3, point.getY() - delta * 3);
      OdGeCircArc2d cirArc = new OdGeCircArc2d();
      cirArc.setCenter(cenPt);
      cirArc.setRadius(delta);
      cirArc.setAngles(0.0, Math.PI * 2);

      EdgeArray edgePtrs = new EdgeArray();
      edgePtrs.add(cirArc);
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), edgePtrs);
      //GC.SuppressFinalize(cirArc); // NB: appendLoop takes ownership of the edges

      /**********************************************************************/
      /* Create a circular Hatch                                            */
      /**********************************************************************/
      pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(pDb);
      OdDbObjectId redHatchId = bBTR.appendOdDbEntity(pHatch);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pHatch.setAssociative(false);
      pHatch.setPattern(OdDbHatch.HatchPatternType.kPreDefined, "SOLID");
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);
      OdCmColor col = new OdCmColor();
      col.setRGB((byte)255, (byte)0, (byte)0);
      pHatch.setColor(col);

      /**********************************************************************/
      /* Define an outer loop with an array of edges                        */
      /**********************************************************************/
      cirArc = new OdGeCircArc2d();
      cirArc.setCenter(cenPt.subtract(new OdGeVector2d(delta, 0.0)));
      cirArc.setRadius(delta);
      cirArc.setAngles(0.0, Math.PI * 2);
      edgePtrs.clear();
      edgePtrs.add(cirArc);
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), edgePtrs);
      //GC.SuppressFinalize(cirArc); // NB: appendLoop takes ownership of the edges

      /**********************************************************************/
      /* Create a circular Hatch                                            */
      /**********************************************************************/
      pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(pDb);
      OdDbObjectId greenHatchId = bBTR.appendOdDbEntity(pHatch);

      pHatch.setAssociative(false);
      pHatch.setPattern(OdDbHatch.HatchPatternType.kPreDefined, "SOLID");
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);
      col.setRGB((byte)0, (byte)255, (byte)0);
      pHatch.setColor(col);

      /**********************************************************************/
      /* Define an outer loop with an array of edges                        */
      /**********************************************************************/
      cirArc = new OdGeCircArc2d();
      cirArc.setCenter(cenPt.subtract(new OdGeVector2d(0.0, delta)));
      cirArc.setRadius(delta);
      cirArc.setAngles(0.0, Math.PI * 2);
      edgePtrs.clear();
      edgePtrs.add(cirArc);
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), edgePtrs);
      //GC.SuppressFinalize(cirArc); // NB: appendLoop takes ownership of the edges

      /**********************************************************************/
      /* Use the SortentsTable to manipulate draw order                     */
      /*                                                                    */
      /* The draw order now is white, red, green                            */
      /**********************************************************************/
      OdDbSortentsTable pSET = bBTR.getSortentsTable();

      /**********************************************************************/
      /* Move the green hatch below the red hatch                           */
      /* The draw order now is white, green, red                            */
      /**********************************************************************/
      OdDbObjectIdArray id = new OdDbObjectIdArray();
      id.add(greenHatchId);
      pSET.moveBelow(id, redHatchId);

      /**********************************************************************/
      /* Create an associative user-defined hatch                           */
      /**********************************************************************/
      pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(pDb);
      OdDbObjectId hatchId = bBTR.appendOdDbEntity(pHatch);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pHatch.setAssociative(true);
      pHatch.setDatabaseDefaults(pDb); // make hatch aware of DB for the next call
      pHatch.setPattern(OdDbHatch.HatchPatternType.kUserDefined, "_USER");
      pHatch.setPatternSpace(0.125);
      pHatch.setPatternAngle(OdaToRadian(30.0));
      pHatch.setPatternDouble(true);
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);

      /**********************************************************************/
      /* Define the loops                                                */
      /**********************************************************************/
      OdDbObjectIdArray loopIds = new OdDbObjectIdArray();
      OdDbEllipse pEllipse = OdDbEllipse.createObject();
      pEllipse.setDatabaseDefaults(pDb);
      loopIds.add(bBTR.appendOdDbEntity(pEllipse));

      OdGePoint3d centerPt = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      centerPt.setX(centerPt.getX() + delta);
      centerPt.setY(centerPt.getY() + (delta * 1.5));
      pEllipse.set(centerPt, OdGeVector3d.getKZAxis(), new OdGeVector3d(delta, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Append the loops to the hatch                                      */
      /**********************************************************************/
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), loopIds);

      /**********************************************************************/
      /* Define a custom hatch pattern "MY_STARS"                           */
      /**********************************************************************/
      OdHatchPattern stars = new OdHatchPattern();
      OdHatchPatternLine line = new OdHatchPatternLine();

      line.setM_dLineAngle(0.0);
      line.setM_patternOffset(new OdGeVector2d(0, 0.866));
      line.getM_dashes().add(0.5);
      line.getM_dashes().add(-0.5);
      stars.add(line);
      line.setM_dLineAngle(1.0472);
      line.setM_patternOffset(new OdGeVector2d(0, 0.866));
      stars.add(line);
      line.setM_dLineAngle(2.0944);
      line.setM_basePoint(new OdGePoint2d(0.25, 0.433));
      line.setM_patternOffset(new OdGeVector2d(0, 0.866));
      stars.add(line);

      /**********************************************************************/
      /* Register the pattern                                               */
      /**********************************************************************/
      pDb.appServices().patternManager().appendPattern(OdDbHatch.HatchPatternType.kCustomDefined,
        "MY_STARS", stars);

      /**********************************************************************/
      /* Create an associative custom defined hatch                         */
      /**********************************************************************/
      pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(pDb);
      hatchId = bBTR.appendOdDbEntity(pHatch);

      /**********************************************************************/
      /* Set some properties                                                */
      /**********************************************************************/
      pHatch.setAssociative(true);
      pHatch.setDatabaseDefaults(pDb); // make hatch aware of DB for the next call
      pHatch.setPattern(OdDbHatch.HatchPatternType.kCustomDefined, "MY_STARS");
      pHatch.setPatternScale(0.125);
      pHatch.setPatternAngle(OdaToRadian(30.0));
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);

      /**********************************************************************/
      /* Define the loops                                                */
      /**********************************************************************/
      loopIds.clear();
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(pDb);
      loopIds.add(bBTR.appendOdDbEntity(pCircle));

      centerPt = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      centerPt.setX(centerPt.getX() + (delta * 4.0));
      centerPt.setY(centerPt.getY() + delta);
      pCircle.setCenter(centerPt);
      pCircle.setRadius(delta * 1.5);

      /**********************************************************************/
      /* Append the loops to the hatch                                      */
      /**********************************************************************/
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), loopIds);

      try
      {
        /********************************************************************/
        /* Create an associative predefined hatch                           */
        /********************************************************************/
        pHatch = OdDbHatch.createObject();
        pHatch.setDatabaseDefaults(pDb);
        hatchId = bBTR.appendOdDbEntity(pHatch);

        /********************************************************************/
        /* Set some properties                                              */
        /********************************************************************/
        point = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
        // Set the hatch properties.
        pHatch.setAssociative(true);
        pHatch.setDatabaseDefaults(pDb);// make hatch aware of DB for the next call
        pHatch.setPattern(OdDbHatch.HatchPatternType.kPreDefined, "ANGLE");
        pHatch.setPatternScale(0.5);
        pHatch.setPatternAngle(0.5); // near 30 degrees
        pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);


        /********************************************************************/
        /* Define the loops                                                 */
        /********************************************************************/
        loopIds.clear();
        pCircle = OdDbCircle.createObject();
        pCircle.setDatabaseDefaults(pDb);
        loopIds.add(bBTR.appendOdDbEntity(pCircle));
        centerPt.setX(centerPt.getX() - (delta * 2.0))/* delta*3 */;
        centerPt.setY(centerPt.getY() - (delta * 2.5));
        pCircle.setCenter(centerPt);
        pCircle.setRadius(delta * 1.5);

        /********************************************************************/
        /* Append the loops to the hatch                                    */
        /********************************************************************/
        pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), loopIds);
      }
      //catch (OdError e)
      catch(Exception e)
      {
        //System.out.format("\n\nException occurred: %s", e.description());
        System.out.format("\n\nException occurred");
        e.printStackTrace();
        System.out.format("\nHatch with predefined pattern \"ANGLE\" was not added.");
        System.out.format("\nMake sure PAT file with pattern definition is available to Teigha.");
        System.out.format("\nPress ENTER to continue...");
      }
    }
    /************************************************************************/
    /* Add some text entities to the specified BlockTableRecord             */
    /*                                                                      */
    /* The newly created entities are placed in a group                     */
    /************************************************************************/
    void addTextEnts(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      // We want to place all text items into a newly created group, so
      // open the group dictionary here.

      /**********************************************************************/
      /* Open the Group Dictionary                                          */
      /**********************************************************************/
      OdDbDictionary pGroupDic = OdDbDictionary.cast(btrId.database().getGroupDictionaryId().safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create a new Group                                                 */
      /**********************************************************************/
      OdDbGroup pGroup = OdDbGroup.createObject();

      /**********************************************************************/
      /* Add it to the Group Dictionary                                     */
      /**********************************************************************/
      pGroupDic.setAt("OdaGroup", pGroup);

      /**********************************************************************/
      /* Set some properties                                                 */
      /**********************************************************************/
      pGroup.setName("OdaGroup");
      pGroup.setSelectable(true);

      /**********************************************************************/
      /* Get the Lower-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      double dx = w / 16.0;
      double dy = h / 12.0;

      double textHeight = m_EntityBoxes.getHeight() / 12.0;

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "TEXT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Add the text entities, and add them to the group                   */
      /*                                                                    */
      /* Show the relevant positions and alignment points                   */
      /**********************************************************************/
      OdGePoint3d position = point.add(new OdGeVector3d(dx, dy * 9.0, 0.0));
      addPointEnt(bBTR, position, layerId, pGroup);
      addTextEnt(bBTR, position, position,
        "Left Text", textHeight, TextHorzMode.kTextLeft, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      OdGePoint3d alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 9.0, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Center Text", textHeight, TextHorzMode.kTextCenter, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 9.0, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Right Text", textHeight, TextHorzMode.kTextRight, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 8.0, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Middle Text", textHeight, TextHorzMode.kTextMid, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      position = point.add(new OdGeVector3d(dx, dy * 1, 0.0));
      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy, 0.0));
      addPointEnt(bBTR, position, layerId, pGroup);
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, position, alignmentPoint,
        "Aligned Text", textHeight, TextHorzMode.kTextAlign, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      position = point.add(new OdGeVector3d(dx, dy * 5.5, 0.0));
      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 5.5, 0.0));
      addPointEnt(bBTR, position, layerId, pGroup);
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, position, alignmentPoint,
        "Fit Text", textHeight, TextHorzMode.kTextFit, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);


      /**********************************************************************/
      /* Start a new box                                                    */
      /**********************************************************************/
      point = m_EntityBoxes.getBox(boxRow, boxCol + 1);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "TEXT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);
      textHeight = h / 16.0;

      /**********************************************************************/
      /* Create a new anonymous Group                                                 */
      /**********************************************************************/
      pGroup = OdDbGroup.createObject();

      /**********************************************************************/
      /* Add it to the Group Dictionary                                     */
      /**********************************************************************/
      pGroupDic.setAt("*", pGroup);

      /**********************************************************************/
      /* Set some properties                                                 */
      /**********************************************************************/
      pGroup.setName("*");
      pGroup.setAnonymous();
      pGroup.setSelectable(true);

      /**********************************************************************/
      /* Add the text entities, and add them to the group                   */
      /*                                                                    */
      /* Show the relevant positions and alignment points                   */
      /**********************************************************************/
      alignmentPoint = point.add(new OdGeVector3d(dx, dy * 9.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Top Left", textHeight, TextHorzMode.kTextLeft, TextVertMode.kTextTop, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 9.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Top Center", textHeight, TextHorzMode.kTextCenter, TextVertMode.kTextTop, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 9.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Top Right", textHeight, TextHorzMode.kTextRight, TextVertMode.kTextTop, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(dx, dy * 7.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Middle Left", textHeight, TextHorzMode.kTextLeft, TextVertMode.kTextVertMid, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 7.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Middle Center", textHeight, TextHorzMode.kTextCenter, TextVertMode.kTextVertMid, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 7.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Middle Right", textHeight, TextHorzMode.kTextRight, TextVertMode.kTextVertMid, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(dx, dy * 5.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Baseline Left", textHeight, TextHorzMode.kTextLeft, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 5.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Baseline Center", textHeight, TextHorzMode.kTextCenter, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 5.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Baseline Right", textHeight, TextHorzMode.kTextRight, TextVertMode.kTextBase, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(dx, dy * 3.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Bottom Left", textHeight, TextHorzMode.kTextLeft, TextVertMode.kTextBottom, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w / 2.0, dy * 3.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Bottom Center", textHeight, TextHorzMode.kTextCenter, TextVertMode.kTextBottom, OdDbObjectId.getKNull(), styleId, pGroup);

      alignmentPoint = point.add(new OdGeVector3d(w - dx, dy * 3.5, 0.0));
      addPointEnt(bBTR, alignmentPoint, layerId, pGroup);
      addTextEnt(bBTR, alignmentPoint, alignmentPoint,
        "Bottom Right", textHeight, TextHorzMode.kTextRight, TextVertMode.kTextBottom, OdDbObjectId.getKNull(), styleId, pGroup);
    }
    /************************************************************************/
    /* Add a Solid to the specified BlockTableRecord                          */
    /************************************************************************/
    void addSolid(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "SOLID", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a Solid                                                   */
      /**********************************************************************/
      OdDbSolid pSolid = OdDbSolid.createObject();
      pSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pSolid);

      pSolid.setPointAt(0, point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0)));
      pSolid.setPointAt(1, point.add(new OdGeVector3d(w * 7.0 / 8.0, h * 1.0 / 8.0, 0.0)));
      pSolid.setPointAt(2, point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 6.0 / 8.0, 0.0)));
      pSolid.setPointAt(3, point.add(new OdGeVector3d(w * 7.0 / 8.0, h * 6.0 / 8.0, 0.0)));
    }
    /************************************************************************/
    /* Add a Ray to the specified BlockTableRecord                          */
    /************************************************************************/
    void addRay(OdDbObjectId btrId,
                          int boxRow,
                          int boxCol,
                          OdDbObjectId layerId,
                          OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "RAY", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a Ray from the center of the box and passing through        */
      /* the lower-left corner of the box                                   */
      /**********************************************************************/
      OdDbRay pRay = OdDbRay.createObject();
      pRay.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pRay);

      OdGePoint3d basePoint = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      OdGeVector3d unitDir = (point.subtract(basePoint)).normalize();

      pRay.setBasePoint(basePoint);
      pRay.setUnitDir(unitDir);
    }
    /************************************************************************/
    /* Add Ordinate Dimensions to the specified BlockTableRecord            */
    /************************************************************************/
    void addOrdinateDimensions(OdDbObjectId btrId,
                                         int boxRow,
                                         int boxCol,
                                         OdDbObjectId layerId,
                                         OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Ordinate", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      double dx = w / 8.0;
      double dy = h / 8.0;
      /**********************************************************************/
      /* Create a line to be dimensioned                                    */
      /**********************************************************************/
      OdDbLine pLine = OdDbLine.createObject();
      pLine.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pLine);

      OdGePoint3d point1 = point.add(new OdGeVector3d(dx, dy, 0.0));
      OdGePoint3d point2 = point.add(new OdGeVector3d(0.0, 1.5, 0));
      pLine.setStartPoint(point1);
      pLine.setEndPoint(point2);

      /**********************************************************************/
      /* Create the base ordinate dimension                                 */
      /**********************************************************************/
      OdDbOrdinateDimension pDimension = OdDbOrdinateDimension.createObject();
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/

      OdGePoint3d startPoint = new OdGePoint3d();
      OdGePoint3d endPoint = new OdGePoint3d();
      pLine.getStartPoint(startPoint);
      pLine.getEndPoint(endPoint);

      OdGePoint3d leaderEndPoint = startPoint.add(new OdGeVector3d(3.0 * dx, 0, 0.0));
      pDimension.setOrigin(startPoint);
      pDimension.setDefiningPoint(startPoint);
      pDimension.setLeaderEndPoint(leaderEndPoint);
      pDimension.useYAxis();

      /**********************************************************************/
      /* Create an ordinate dimension                                       */
      /**********************************************************************/
      pDimension = OdDbOrdinateDimension.createObject();
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      leaderEndPoint = endPoint.add(new OdGeVector3d(3.0 * dx, -dy, 0.0));

      pDimension.setOrigin(startPoint);
      pDimension.setDefiningPoint(endPoint);
      pDimension.setLeaderEndPoint(leaderEndPoint);
      pDimension.useYAxis();
    }
    /************************************************************************/
    /* Add a Spline to the specified BlockTableRecord                       */
    /************************************************************************/
    void addSpline(OdDbObjectId btrId,
                             int boxRow,
                             int boxCol,
                             OdDbObjectId layerId,
                             OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "SPLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create Spline                                                      */
      /**********************************************************************/
      OdDbSpline pSpline = OdDbSpline.createObject();
      pSpline.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pSpline);

      /**********************************************************************/
      /* Create the fit points                                              */
      /**********************************************************************/

      double dx = w / 8.0;
      double dy = h / 8.0;

      OdGePoint3dArray fitPoints = new OdGePoint3dArray();
      fitPoints.add(point.add(new OdGeVector3d(1.0 * dx, 1.0 * dy, 0.0)));
      fitPoints.add(point.add(new OdGeVector3d(3.0 * dx, 6.0 * dy, 0.0)));
      fitPoints.add(point.add(new OdGeVector3d(4.0 * dx, 2.0 * dy, 0.0)));
      fitPoints.add(point.add(new OdGeVector3d(7.0 * dx, 7.0 * dy, 0.0)));

      pSpline.setFitData(
        fitPoints,                    // Fit Points
        3,                            // Degree
        0.0,                          // Fit tolerance
        new OdGeVector3d(0.0, 0.0, 0.0),  // startTangent
        new OdGeVector3d(1.0, 0.0, 0.0)); // endTangent
    }
    /************************************************************************/
    /* Add some Traces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addTraces(OdDbObjectId btrId,
                             int boxRow,
                             int boxCol,
                             OdDbObjectId layerId,
                             OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "TRACEs", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a Trace                                                     */
      /**********************************************************************/
      OdDbTrace pTrace = OdDbTrace.createObject();
      pTrace.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pTrace);

      double dx = w / 8.0;
      double dy = h / 8.0;
      pTrace.setPointAt(0, point.add(new OdGeVector3d(1.0 * dx, 2.0 * dx, 0.0)));
      pTrace.setPointAt(1, point.add(new OdGeVector3d(1.0 * dx, 1.0 * dx, 0.0)));
      pTrace.setPointAt(2, point.add(new OdGeVector3d(6.0 * dx, 2.0 * dx, 0.0)));
      pTrace.setPointAt(3, point.add(new OdGeVector3d(7.0 * dx, 1.0 * dx, 0.0)));

      /**********************************************************************/
      /* Create a Trace                                                     */
      /**********************************************************************/
      pTrace = OdDbTrace.createObject();
      pTrace.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pTrace);

      pTrace.setPointAt(0, point.add(new OdGeVector3d(6.0 * dx, 2.0 * dx, 0.0)));
      pTrace.setPointAt(1, point.add(new OdGeVector3d(7.0 * dx, 1.0 * dx, 0.0)));
      pTrace.setPointAt(2, point.add(new OdGeVector3d(6.0 * dx, 7.0 * dy, 0.0)));
      pTrace.setPointAt(3, point.add(new OdGeVector3d(7.0 * dx, 7.0 * dy, 0.0)));

    }
    /************************************************************************/
    /* Add a Polyline to the specified BlockTableRecord                     */
    /************************************************************************/
    void addPolyline(OdDbObjectId btrId,
                               int boxRow,
                               int boxCol,
                               OdDbObjectId layerId,
                               OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "LWPOLYLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a polyline                                                  */
      /**********************************************************************/
      OdDbPolyline pPolyline = OdDbPolyline.createObject();
      pPolyline.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pPolyline);

      /**********************************************************************/
      /* Create the vertices                                                */
      /**********************************************************************/

      double dx = w / 8.0;
      double dy = h / 8.0;

      OdGePoint2d point2d = new OdGePoint2d(point.getX() + 1.5 * dx, point.getY() + 3.0 * dy);

      pPolyline.addVertexAt(0, point2d);

      point2d.setY(point2d.getY() - (0.5 * dy));
      pPolyline.addVertexAt(1, point2d);
      pPolyline.setBulgeAt(1, 1.0);

      point2d.setX(point2d.getX() + (5.0 * dx));
      pPolyline.addVertexAt(2, point2d);

      point2d.setY(point2d.getY() + (4.0 * dy));
      pPolyline.addVertexAt(3, point2d);

      point2d.setX(point2d.getX() - (1.0 * dx));
      pPolyline.addVertexAt(4, point2d);

      point2d.setY(point2d.getY() - (4.0 * dy));
      pPolyline.addVertexAt(5, point2d);
      pPolyline.setBulgeAt(5, -1.0);

      point2d.setX(point2d.getX() - (3.0 * dx));
      pPolyline.addVertexAt(6, point2d);

      point2d.setY(point2d.getY() + (0.5 * dy));
      pPolyline.addVertexAt(7, point2d);

      pPolyline.setClosed(true);
    }
    /************************************************************************/
    /* Add Arc Aligned Text to the specified BlockTableRecord               */
    /************************************************************************/
    void addArcText(OdDbObjectId btrId,
                              int boxRow,
                              int boxCol,
                              OdDbObjectId layerId,
                              OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "ARCALIGNED-", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "TEXT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);


      /**********************************************************************/
      /* Create an arc                                                       */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pArc);

      //  double dx   = w / 8.0;
      double dy = h / 8.0;

      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol).add(new OdGeVector3d(0.0, -2.0 * dy, 0));
      pArc.setCenter(center);
      pArc.setRadius(3.0 * dy);
      pArc.setStartAngle(OdaToRadian(45.0));
      pArc.setEndAngle(OdaToRadian(135.0));

      /**********************************************************************/
      /* Create the ArcAlignedText                                          */
      /**********************************************************************/
      OdDbArcAlignedText pArcText = OdDbArcAlignedText.createObject();
      pArcText.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pArcText);

      pArcText.setTextString("ArcAligned");
      pArcText.setArcId(pArc.objectId());
      pArcText.setTextStyle(styleId);
    }
    /************************************************************************/
    /* Add a Wipeout to to the specified BlockTableRecord                   */
    /************************************************************************/
    void addWipeout(OdDbObjectId btrId,
                              int boxRow,
                              int boxCol,
                              OdDbObjectId layerId,
                              OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the lower-left corner and center of the box                    */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "WIPEOUT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a hatch object to be wiped out                              */
      /**********************************************************************/
      OdDbHatch pHatch = OdDbHatch.createObject();
      pHatch.setDatabaseDefaults(bBTR.database());
      OdDbObjectId hatchId = bBTR.appendOdDbEntity(pHatch);

      /**********************************************************************/
      /* Create a hatch object to be wiped out                              */
      /**********************************************************************/
      pHatch.setAssociative(true);
      pHatch.setPattern(OdDbHatch.HatchPatternType.kUserDefined, "_USER");
      pHatch.setPatternSpace(0.125);
      pHatch.setPatternAngle(0.5); // near 30 degrees
      pHatch.setPatternDouble(true); // Cross hatch
      pHatch.setHatchStyle(OdDbHatch.HatchStyle.kNormal);

      /**********************************************************************/
      /* Create an outer loop for the hatch                                 */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());
      OdDbObjectIdArray loopIds = new OdDbObjectIdArray();
      loopIds.add(bBTR.appendOdDbEntity(pCircle));
      pCircle.setCenter(center);
      pCircle.setRadius(Math.min(w, h) * 0.4);
      pHatch.appendLoop((int)OdDbHatch.HatchLoopType.kDefault.swigValue(), loopIds);

      /**********************************************************************/
      /* Create the wipeout                                                  */
      /**********************************************************************/
      OdDbWipeout pWipeout = OdDbWipeout.createObject();
      pWipeout.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pWipeout);

      OdGePoint3dArray boundary = new OdGePoint3dArray();
      boundary.add(center.add(new OdGeVector3d(-w * 0.4, -h * 0.4, 0.0)));
      boundary.add(center.add(new OdGeVector3d(w * 0.4, -h * 0.4, 0.0)));
      boundary.add(center.add(new OdGeVector3d(0.0, h * 0.4, 0.0)));
      boundary.add(center.add(new OdGeVector3d(-w * 0.4, -h * 0.4, 0.0)));

      pWipeout.setBoundary(boundary);

      pWipeout.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kShow, true);
      pWipeout.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kClip, true);
      pWipeout.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kShowUnAligned, true);
      pWipeout.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kTransparent, false);
    }
    /************************************************************************/
    /* Add a RadialDimensionLarge to the specified BlockTableRecord         */
    /************************************************************************/
    void addRadialDimensionLarge(OdDbObjectId btrId,
                                           int boxRow,
                                           int boxCol,
                                           OdDbObjectId layerId,
                                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Radial", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dim Large", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create an arc to be dimensioned                                    */
      /**********************************************************************/
      OdDbArc pArc = OdDbArc.createObject();
      pArc.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pArc);

      OdGePoint3d center = point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0));
      pArc.setRadius(2.0);

      pArc.setCenter(center);
      pArc.setStartAngle(OdaToRadian(30.0));
      pArc.setEndAngle(OdaToRadian(90.0));

      /**********************************************************************/
      /* Create RadialDimensionLarge                                        */
      /**********************************************************************/
      OdDbRadialDimensionLarge pDimension = OdDbRadialDimensionLarge.createObject();
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      OdGePoint3d centerPoint, chordPoint, overrideCenter, jogPoint, textPosition;

      // The centerPoint of the dimension is the center of the arc
      centerPoint = pArc.center();

      // The chordPoint of the dimension is the midpoint of the arc
      chordPoint = centerPoint.add(new OdGeVector3d(pArc.radius(), 0.0, 0.0).rotateBy(0.5 * (pArc.startAngle() + pArc.endAngle()), OdGeVector3d.getKZAxis()));

      // The overrideCenter is just to the right of the actual center
      overrideCenter = centerPoint.add(new OdGeVector3d(w * 3.0 / 8.0, 0.0, 0.0));

      // The jogPoint is halfway between the overrideCenter and the chordCoint
      jogPoint = overrideCenter.add(new OdGeVector3d(chordPoint.subtract(overrideCenter)).multiply(0.5));

      // The textPosition is along the vector between the centerPoint and the chordPoint.
      textPosition = centerPoint.add(new OdGeVector3d(chordPoint.subtract(centerPoint)).multiply(0.7));

      double jogAngle = OdaToRadian(45.0);

      pDimension.setCenter(centerPoint);
      pDimension.setChordPoint(chordPoint);
      pDimension.setOverrideCenter(overrideCenter);
      pDimension.setJogPoint(jogPoint);
      pDimension.setTextPosition(textPosition);
      pDimension.setJogAngle(jogAngle);

    }
    /************************************************************************/
    /* Add a 2 Line Angular Dimension to the specified BlockTableRecord     */
    /************************************************************************/
    void add2LineAngularDimension(OdDbObjectId btrId,
                                             int boxRow,
                                             int boxCol,
                                             OdDbObjectId layerId,
                                             OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "2 Line Angular", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create the lines to be dimensioned                                 */
      /**********************************************************************/
      OdGePoint3d center = point.add(new OdGeVector3d(w * 1.0 / 8.0, h * 1.0 / 8.0, 0.0));
      OdGeVector3d v1 = new OdGeVector3d(w * 1.0 / 8.0, 0.0, 0.0);
      OdGeVector3d v2 = new OdGeVector3d(w * 4.0 / 8.0, 0.0, 0.0);
      OdGeVector3d v3 = v2.add(new OdGeVector3d(0.45, 0.0, 0.0));

      OdDbLine pLine1 = OdDbLine.createObject();
      pLine1.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pLine1);
      pLine1.setStartPoint(center.add(v1));
      pLine1.setEndPoint(center.add(v2));

      double rot = OdaToRadian(75.0);
      v1.rotateBy(rot, OdGeVector3d.getKZAxis());
      v2.rotateBy(rot, OdGeVector3d.getKZAxis());

      OdDbLine pLine2 = OdDbLine.createObject();
      pLine2.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pLine2);
      pLine2.setStartPoint(center.add(v1));
      pLine2.setEndPoint(center.add(v2));

      /**********************************************************************/
      /* Create 2 Line Angular Dimensionn                                   */
      /**********************************************************************/
      OdDb2LineAngularDimension pDimension = OdDb2LineAngularDimension.createObject();
      bBTR.appendOdDbEntity(pDimension);

      /**********************************************************************/
      /* Use the default dim variables                                      */
      /**********************************************************************/
      pDimension.setDatabaseDefaults(pDb);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/

      v3.rotateBy(rot / 2.0, OdGeVector3d.getKZAxis());
      pDimension.setArcPoint(center.add(v3));

      OdGePoint3d startPoint = new OdGePoint3d();
      pLine1.getStartPoint(startPoint);
      pDimension.setXLine1Start(startPoint);

      OdGePoint3d endPoint = new OdGePoint3d();
      pLine1.getEndPoint(endPoint);
      pDimension.setXLine1End(endPoint);

      //  pDimension.setArcPoint(endPoint + 0.45*(endPoint.subtract(startPoint)).normalize());

      pLine2.getStartPoint(startPoint);
      pDimension.setXLine2Start(startPoint);

      pLine2.getEndPoint(endPoint);
      pDimension.setXLine2End(endPoint);
    }
    /************************************************************************/
    /* Add an ACIS Solid to the specified BlockTableRecord                  */
    /************************************************************************/
    void addACIS(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double h    = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "3DSOLID", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDbEntityPtrArray entities = new OdDbEntityPtrArray();
      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      /**********************************************************************/
      /* Read the solids in the .sat file                                   */
      /**********************************************************************/
      if (OdDbBody.acisIn("OdWriteEx.sat", entities) == OdResult.eOk)
      {
        /********************************************************************/
        /* Read the solids in the .sat file                                 */
        /********************************************************************/
        addTextEnt(bBTR,
          (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
          "from SAT file", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
        for (OdDbEntity p3dSolid : entities)
        {
          /******************************************************************/
          /* Move the solid into the center of the box                      */
          /******************************************************************/
          OdDbObjectId id = bBTR.appendOdDbEntity(p3dSolid);
          p3dSolid.transformBy(xfm);
          //p3dSolid.Dispose();
          /******************************************************************/
          /* Each of these entities will later get its own viewport         */
          /******************************************************************/
          m_layoutEntities.add(id);
        }
      }
      else
      {
        /********************************************************************/
        /* Create a simple solid                                            */
        /********************************************************************/
        OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
        p3dSolid.setDatabaseDefaults(bBTR.database());
        OdDbObjectId id = bBTR.appendOdDbEntity(p3dSolid);

        p3dSolid.createSphere(1.0);
        p3dSolid.transformBy(xfm);

        /********************************************************************/
        /* This entity will later get its own viewport                      */
        /********************************************************************/
        m_layoutEntities.add(id);
      }
    }
    /************************************************************************/
    /* Add an Image to the specified BlockTableRecord                       */
    /************************************************************************/
    void addImage(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Open the Image Dictionary                                          */
      /**********************************************************************/
      OdDbObjectId imageDictId = OdDbRasterImageDef.createImageDictionary(pDb);
      OdDbDictionary pImageDict = OdDbDictionary.cast(imageDictId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create an ImageDef object                                          */
      /**********************************************************************/
      OdDbRasterImageDef pImageDef = OdDbRasterImageDef.createObject();
      OdDbObjectId imageDefId = pImageDict.setAt("OdWriteEx", pImageDef);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pImageDef.setSourceFileName("OdWriteEx.jpg");
      // Use next line to set image size manually without loading actual raster file.
      // This method sets "dummy" image instead. It's OK for saving drawing to DXF/DWG.
      // But image will not be rendered/exported to other formats without file saving and opening again
      pImageDef.setImage(OdGiRasterImageDesc.createObject(1024, 650, OdGiRasterImage.Units.kInch));

      // Use next line to set size from the actual raster file.
      // This is also required if you are going to render/export the drawing immediately
      // without saving to DWG and loading again
      //pImageDef.image();    // Force image loading from file (findFile() should be able to locate the image).


      /**********************************************************************/
      /* Create an Image object                                             */
      /**********************************************************************/
      OdDbRasterImage pImage = OdDbRasterImage.createObject();
      pImage.setDatabaseDefaults(pDb);
      bBTR.appendOdDbEntity(pImage);

      /**********************************************************************/
      /* Set some parameters                                                */
      /**********************************************************************/
      pImage.setImageDefId(imageDefId);
      pImage.setOrientation(point, new OdGeVector3d(w, 0, 0), new OdGeVector3d(0.0, h, 0));
      pImage.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kShow, true);
      pImage.setDisplayOpt(OdDbRasterImage.ImageDisplayOpt.kShowUnAligned, true);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      point = m_EntityBoxes.getBox(boxRow, boxCol);
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "IMAGE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
    }
    /************************************************************************/
    /* Add an XRef to the specified BlockTableRecord                        */
    /************************************************************************/
    void addXRef(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
      OdDbDatabase pDb = btrId.database();

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "XREF INSERT", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Open the block table                                               */
      /**********************************************************************/
      OdDbBlockTable pBlocks = OdDbBlockTable.cast(pDb.getBlockTableId().safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Create a BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord pXRef = OdDbXRefManExt.addNewXRefDefBlock(pDb, "OdWriteEx XRef.dwg", "XRefBlock", false);

      /**********************************************************************/
      /* Insert the Xref                                                    */
      /**********************************************************************/
      OdDbObjectId xRefId = addInsert(bBTR, pXRef.objectId(), 1.0, 1.0);

      /**********************************************************************/
      /* Open the insert                                                    */
      /**********************************************************************/
      OdDbBlockReference pXRefIns = OdDbBlockReference.cast(xRefId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Set the insertion point                                            */
      /**********************************************************************/
      pXRefIns.setPosition(point);

      /**********************************************************************/
      /* Move\Scale XREF to presentation rectangle                          */
      /**********************************************************************/

      OdGeExtents3d extents = new OdGeExtents3d();
      if (pXRefIns.getGeomExtents(extents) == OdResult.eOk && extents.isValidExtents())
      {
        double dScale = Math.min(w / (extents.maxPoint().getX() - extents.minPoint().getX()), h * (7.0 / 8.0) / (extents.maxPoint().getY() - extents.minPoint().getY()));
        pXRefIns.setScaleFactors(new OdGeScale3d(dScale, dScale, 1));
        pXRefIns.setPosition(point.subtract((extents.minPoint().subtract(point.asVector())).asVector().multiply(dScale)));
      }
    }
    /************************************************************************/
    /* Add a Table to the specified BlockTableRecord                        */
    /************************************************************************/
    void addTable(OdDbObjectId btrId,
                            OdDbObjectId addedBlockId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord pRecord = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the lower-left corner and center of the box                    */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Create the Table                                                  */
      /**********************************************************************/
      OdDbTable pAcadTable = OdDbTable.createObject();
      OdDbObjectId tableId = pRecord.appendOdDbEntity(pAcadTable);
      /**********************************************************************/
      /* This entity will later get its own viewport                        */
      /**********************************************************************/
      m_layoutEntities.add(tableId);

      /**********************************************************************/
      /* Set the parameters                                                 */
      /**********************************************************************/
      pAcadTable.setDatabaseDefaults(pRecord.database());
      pAcadTable.setNumColumns(3);
      pAcadTable.setNumRows(4);

      pAcadTable.generateLayout();
      pAcadTable.setColumnWidth(w / pAcadTable.numColumns());
      pAcadTable.setRowHeight(h / pAcadTable.numRows());

      pAcadTable.setPosition(point);
      pAcadTable.setTextStyle(styleId);

      pAcadTable.setTextHeight(0.500 * pAcadTable.rowHeight(0), (long)RowType.kTitleRow.swigValue());
      pAcadTable.setTextHeight(0.300 * pAcadTable.rowHeight(1), (long)RowType.kHeaderRow.swigValue());
      pAcadTable.setTextHeight(0.250 * pAcadTable.rowHeight(2), (long)RowType.kDataRow.swigValue());

      /**********************************************************************/
      /* Set the alignments                                                 */
      /**********************************************************************/
      for (long row = 1; row < pAcadTable.numRows(); row++)
      {
        for (long col = 0; col < pAcadTable.numColumns(); col++)
        {
          pAcadTable.setAlignment(row, col, CellAlignment.kMiddleCenter);
        }
      }

      /**********************************************************************/
      /* Define the title row                                               */
      /**********************************************************************/
      pAcadTable.mergeCells(0, 0, 0, pAcadTable.numColumns() - 1);
      pAcadTable.setTextString(0, 0, "Title of TABLE");

      /**********************************************************************/
      /* Define the header row                                              */
      /**********************************************************************/
      pAcadTable.setTextString(1, 0, "Header0");
      pAcadTable.setTextString(1, 1, "Header1");
      pAcadTable.setTextString(1, 2, "Header2");

      /**********************************************************************/
      /* Define the first data row                                          */
      /**********************************************************************/
      pAcadTable.setTextString(2, 0, "Data0");
      pAcadTable.setTextString(2, 1, "Data1");
      pAcadTable.setTextString(2, 2, "Data2");

      /**********************************************************************/
      /* Define the second data row                                         */
      /**********************************************************************/
      pAcadTable.setCellType(3, 0, CellType.kBlockCell);
      pAcadTable.setBlockTableRecordId(3, 0, addedBlockId);
      pAcadTable.setBlockScale(3, 0, 1.0);
      pAcadTable.setAutoScale(3, 0, true);
      pAcadTable.setBlockRotation(3, 0, 0.0);

      pAcadTable.setTextString(3, 1, "<-Block Cell.");

      pAcadTable.setCellType(3, 2, CellType.kBlockCell);
      pAcadTable.setBlockTableRecordId(3, 2, addedBlockId);
      pAcadTable.setAutoScale(3, 2, true);
      pAcadTable.setBlockRotation(3, 2, OdaToRadian(30.0));

      pAcadTable.recomputeTableBlock();

      /**********************************************************************/
      /* Add the label                                                     */
      /**********************************************************************/
      addTextEnt(pRecord,
        point.add(m_textOffset), point.add(m_textOffset),
        "ACAD_TABLE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
    }
    /************************************************************************/
    /* Add a Diametric Dimension to the specified BlockTableRecord             */
    /************************************************************************/
    void addDiametricDimension(OdDbObjectId btrId,
                                         int boxRow,
                                         int boxCol,
                                         OdDbObjectId layerId,
                                         OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Diametric", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      addTextEnt(bBTR,
        (point.add(m_textOffset)).add(m_textLine), (point.add(m_textOffset)).add(m_textLine),
        "Dimension", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a circle to be dimensioned                                    */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pCircle);
      pCircle.setCenter(point.add(new OdGeVector3d(0.625, h * 3.0 / 8.0, 0)));
      pCircle.setRadius(0.5);

      /**********************************************************************/
      /* Create a Diametric Dimension                                       */
      /**********************************************************************/
      OdDbDiametricDimension pDimension = OdDbDiametricDimension.createObject();
      pDimension.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pDimension);

      OdGeVector3d chordVector = new OdGeVector3d(pCircle.radius(), 0.0, 0.0);
      chordVector.rotateBy(OdaToRadian(75.0), OdGeVector3d.getKZAxis());

      pDimension.setChordPoint(pCircle.center().add(chordVector));
      pDimension.setFarChordPoint(pCircle.center().subtract(chordVector));
      pDimension.setLeaderLength(0.125);
      pDimension.useDefaultTextPosition();
    }
    /************************************************************************/
    /* Add a Shape to the specified BlockTableRecord                        */
    /************************************************************************/
    void addShape(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the labels                                                     */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Shape", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the center of the box                                          */
      /**********************************************************************/
      OdGePoint3d pCenter = m_EntityBoxes.getBoxCenter(boxRow, boxCol);

      /**********************************************************************/
      /* Create a Shape                                                     */
      /**********************************************************************/
      OdDbShape pShape = OdDbShape.createObject();
      pShape.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pShape);
      double size = w * 3.0 / 8.0;
      point.setY(point.getY() - size);
      pShape.setSize(size);
      pShape.setPosition(pCenter);
      pShape.setRotation(OdaToRadian(90.0));
      pShape.setName("CIRC1");
    }
    /************************************************************************/
    /* Add a MInsert to the specified BlockTableRecord                      */
    /************************************************************************/
    void addMInsert(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId, OdDbObjectId insertId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
          "MInsert", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Add MInsert to the database                                        */
      /**********************************************************************/
      OdDbMInsertBlock pMInsert = OdDbMInsertBlock.createObject();
      pMInsert.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pMInsert);

      /**********************************************************************/
      /* Set some Parameters                                                */
      /**********************************************************************/
      pMInsert.setBlockTableRecord(insertId);
      OdGePoint3d insPnt = point.add(new OdGeVector3d(w * 2.0 / 8.0, h * 2.0 / 8.0, 0.0));
      pMInsert.setPosition(insPnt);
      pMInsert.setScaleFactors(new OdGeScale3d(2.0 / 8.0));
      pMInsert.setRows(2);
      pMInsert.setColumns(3);
      pMInsert.setRowSpacing(h * 4.0 / 8.0);
      pMInsert.setColumnSpacing(w * 2.0 / 8.0);
    }
    /************************************************************************/
    /* Add an Xline to the specified BlockTableRecord                       */
    /************************************************************************/
    void addXline(OdDbObjectId btrId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      //  double w    = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "XLINE", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Get the lower-left corner of the box                               */
      /**********************************************************************/
      point.setY(point.getY() - h);

      /**********************************************************************/
      /* Create a Ray from the center of the box and passing through        */
      /* the lower-left corner of the box                                   */
      /**********************************************************************/
      OdDbXline pXline = OdDbXline.createObject();
      pXline.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pXline);

      OdGePoint3d basePoint = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      OdGeVector3d unitDir = (point.subtract(basePoint)).normalize();

      pXline.setBasePoint(basePoint);
      pXline.setUnitDir(unitDir);
    }
    void addCustomObjects(OdDbDatabase pDb)
    {
      //Open the main dictionary
      OdDbDictionary pMain =
        OdDbDictionary.cast(pDb.getNamedObjectsDictionaryId().safeOpenObject(OpenMode.kForWrite));

      // Create the new dictionary.
      OdDbDictionary pOdtDic = OdDbDictionary.createObject();

      // Add new dictionary to the main dictionary.
      OdDbObjectId dicId = pMain.setAt("TEIGHA_OBJECTS", pOdtDic);

      // Create a new xrecord object.
      OdDbXrecord pXRec = OdDbXrecord.createObject();

      // Add the xrecord the owning dictionary.
      OdDbObjectId xrId = pOdtDic.setAt("PROPERTIES_1", pXRec);

      OdResBuf temp;
      OdResBuf pRb = OdResBuf.createObject();
      pRb.setRestype(1000);
      temp = pRb;// = OdResBuf.newRb(1000);
      temp.setString("Sample XRecord Data");

      temp = appendXDataPair(temp, 40);
      temp.setDouble(3.14159);

      temp = appendXDataPair(temp, 70);
      temp.setInt16((short)312);

      pXRec.setFromRbChain(pRb);
    } //end addCustomObjects
    /************************************************************************/
    /* Add an OLE object to the specified BlockTableRecord                  */
    /************************************************************************/
    void addOLE2FrameFromFile(OdDbObjectId btrId,
                                        int boxRow,
                                        int boxCol,
                                        String fileName,
                                        OdDbObjectId layerId,
                                        OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord pBlock = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the lower-left corner and center of the box                    */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      OdDbOle2Frame pOle2Frame = null;
  /**********************************************************************/
  /* Create an ole2frame entity from arbitrary file using Windows OLE RT*/
  /**********************************************************************/
  // pOle2Frame = OleWrappers.CreateFromFile(inCurrentFolder(fileName));

      /**********************************************************************/
      /* Create an ole2frame entity in a platform-neutral manner            */
      /* Important: open file that is a compound document.                  */
      /* OLE2 frame can't be created from arbitrary file such way.          */
      /**********************************************************************/

      try
      {
        OdStreamBuf pFile =
          TD_Db.odSystemServices().createFile(fileName + ".ole", FileAccessMode.kFileRead,
                                        FileShareMode.kShareDenyReadWrite, FileCreationDisposition.kOpenExisting);

        pOle2Frame = OdDbOle2Frame.createObject();

        OdOleItemHandler pHandler = pOle2Frame.getItemHandler();

        pHandler.setCompoundDocument((long)pFile.length(), pFile);

        pHandler.setDrawAspect(OdOleItemHandler.DvAspect.kContent);

        pOle2Frame.unhandled_setHimetricSize(6879, 3704);
      }
      //catch (OdError err)
      catch(Exception err)
      {
        System.out.println("Ole file: " + fileName + " not found, no OdDbOle2Frame entity created.");
      }
      if (pOle2Frame != null)
      {
        pOle2Frame.setDatabaseDefaults(pBlock.database());
        pBlock.appendOdDbEntity(pOle2Frame);

        /**********************************************************************/
        /* Add the label                                                      */
        /**********************************************************************/
        addTextEnt(pBlock, point.add(m_textOffset), point.add(m_textOffset),
          "OLE2: " + pOle2Frame.getUserType(), m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);


        /**********************************************************************/
        /* Inscribe OLE frame in entity box                                   */
        /**********************************************************************/
        h += m_textOffset.getY();
        h -= (m_textSize * 1.5);
        center.setY(center.getY() + (m_textOffset.getY() / 2.0));
        center.setY(center.getY() - (m_textSize * 1.5 / 2.0));

        h *= 0.95;
        w *= 0.95;

        h /= 2.0;
        w /= 2.0;

        double oh = pOle2Frame.unhandled_himetricHeight();
        double ow = pOle2Frame.unhandled_himetricWidth();
        if (oh / ow < h / w)
        {
          h = w * oh / ow;
        }
        else
        {
          w = h * ow / oh;
        }

        OdRectangle3d rect = new OdRectangle3d();
        rect.getLowLeft().setX(center.getX() - w);
        rect.getUpLeft().setX(rect.getLowLeft().getX());
        rect.getUpRight().setY(center.getY() + h);
        rect.getUpLeft().setY(rect.getUpRight().getY());
        rect.getLowRight().setX(center.getX() + w);
        rect.getUpRight().setX(rect.getLowRight().getX());
        rect.getLowRight().setY(center.getY() - h);
        rect.getLowLeft().setY(rect.getLowRight().getY());
        pOle2Frame.setPosition(rect);
      }
    }
    /************************************************************************/
    /* Add a Box to the specified BlockTableRecord                          */
    /************************************************************************/
    void addBox(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Box", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
      p3dSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(p3dSolid);

      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      p3dSolid.createBox(w * 6.0 / 8.0, h * 6.0 / 8.0, w * 6.0 / 8.0);
      p3dSolid.transformBy(xfm);
    }
    /************************************************************************/
    /* Add a Frustum to the specified BlockTableRecord                      */
    /************************************************************************/
    void addFrustum(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Frustum", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
      p3dSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(p3dSolid);

      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      p3dSolid.createFrustum(w * 6.0 / 8.0, w * 3.0 / 8.0, h * 3.0 / 8.0, w * 1.0 / 8.0);
      p3dSolid.transformBy(xfm);
    }
    /************************************************************************/
    /* Add a Sphere to the specified BlockTableRecord                       */
    /************************************************************************/
    void addTorus(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double h    = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Torus", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
      p3dSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(p3dSolid);

      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      p3dSolid.createTorus(w * 2.0 / 8.0, w * 1.0 / 8.0);
      p3dSolid.transformBy(xfm);
    }
    /************************************************************************/
    /* Add a Wedge to the specified BlockTableRecord                       */
    /************************************************************************/
    void addWedge(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Wedge", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      OdDb3dSolid p3dSolid = OdDb3dSolid.createObject();
      p3dSolid.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(p3dSolid);

      OdGeMatrix3d xfm = OdGeMatrix3d.translation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector());

      p3dSolid.createWedge(w * 6.0 / 8.0, h * 6.0 / 8.0, w * 6.0 / 8.0);
      p3dSolid.transformBy(xfm);
    }
    /************************************************************************/
    /* Add a Region to the specified BlockTableRecord                       */
    /************************************************************************/
    void addRegion(OdDbObjectId btrId,
                           int boxRow,
                           int boxCol,
                           OdDbObjectId layerId,
                           OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      //  double h    = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Region", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create a Circle                                                    */
      /**********************************************************************/
      OdDbCircle pCircle = OdDbCircle.createObject();
      pCircle.setDatabaseDefaults(bBTR.database());

      OdGePoint3d center = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      pCircle.setCenter(center);
      pCircle.setRadius(w * 3.0 / 8.0);


      /**********************************************************************/
      /* Add it to the array of curves                                      */
      /**********************************************************************/
      OdRxObjectPtrArray curveSegments = new OdRxObjectPtrArray();
      curveSegments.add(pCircle);

      /**********************************************************************/
      /* Create the region                                                  */
      /**********************************************************************/
      OdRxObjectPtrArray regions = new OdRxObjectPtrArray();
      OdResult res = OdDbRegion.createFromCurves(curveSegments, regions);

      /**********************************************************************/
      /* Append it to the block table record                                */
      /**********************************************************************/
      if (res == OdResult.eOk)
      {
        bBTR.appendOdDbEntity(OdDbEntity.cast(regions.get(0)));
      }
    }
    void addHelix(OdDbObjectId blockId,
                            int boxRow,
                            int boxCol,
                            OdDbObjectId layerId,
                            OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(blockId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Helix", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create the Helix                                                   */
      /**********************************************************************/
      OdDbHelix pHelix = OdDbHelix.createObject();
      pHelix.setDatabaseDefaults(bBTR.database());

      /**********************************************************************/
      /* Add the Helix to the database                                      */
      /**********************************************************************/
      bBTR.appendOdDbEntity(pHelix);

      /**********************************************************************/
      /* Set the Helix's parameters                                         */
      /**********************************************************************/
      pHelix.setConstrain(OdDbHelix.ConstrainType.kHeight);
      pHelix.setHeight(h);
      pHelix.setAxisPoint(point.add(new OdGeVector3d(w / 2.0, -h / 2.0, 0.0)));
      pHelix.setStartPoint(pHelix.axisPoint().add(new OdGeVector3d(w / 6.0, 0.0, 0.0)));
      pHelix.setTwist(false);
      pHelix.setTopRadius(w * 3.0 / 8.0);
      pHelix.setTurns(6);

      /**********************************************************************/
      /* Create the Helix geometry (confirm parameters are set)             */
      /**********************************************************************/
      pHelix.createHelix();
    }
    void addDwfUnderlay(OdDbObjectId blockId,
                                  int boxRow,
                                  int boxCol,
                                  OdDbObjectId layerId,
                                  OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(blockId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Dwf reference", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create the Dwf definition                                          */
      /**********************************************************************/
      OdDbDwfDefinition pDwfDef = OdDbDwfDefinition.createObject();
      String itemName = "Unsaved Drawing-Model";
      pDwfDef.setSourceFileName("OdWriteEx.dwf");
      pDwfDef.setItemName(itemName);

      // Post to database
      OdDbObjectId idDef = pDwfDef.postDefinitionToDb(blockId.database(),
                                                          "OdWriteEx - " + itemName);

      /**********************************************************************/
      /* Create the Dwf reference                                           */
      /**********************************************************************/
      OdDbDwfReference pDwfRef = OdDbDwfReference.createObject();
      pDwfRef.setDatabaseDefaults(bBTR.database());

      /**********************************************************************/
      /* Add the Dwf reference to the database                              */
      /**********************************************************************/
      bBTR.appendOdDbEntity(pDwfRef);

      /**********************************************************************/
      /* Set the Dwf reference's parameters                                 */
      /**********************************************************************/
      pDwfRef.setDefinitionId(idDef);
      pDwfRef.setPosition(point.add(new OdGeVector3d(-w / 4, -h / 2, 0.0)));
      pDwfRef.setScaleFactors(new OdGeScale3d(0.001));
    }
    void addDgnUnderlay(OdDbObjectId blockId,
                                  int boxRow,
                                  int boxCol,
                                  OdDbObjectId layerId,
                                  OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(blockId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Dgn reference", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create the Dwf definition                                          */
      /**********************************************************************/
      OdDbDgnDefinition pDgnDef = OdDbDgnDefinition.createObject();
      String itemName = "Model";
      pDgnDef.setSourceFileName("OdWriteEx.dgn");
      pDgnDef.setItemName(itemName);

      // Post to database
      OdDbObjectId idDef = pDgnDef.postDefinitionToDb(blockId.database(),
                                                          "OdWriteEx - " + itemName);

      /**********************************************************************/
      /* Create the Dgn reference                                           */
      /**********************************************************************/
      OdDbDgnReference pDgnRef = OdDbDgnReference.createObject();
      pDgnRef.setDatabaseDefaults(bBTR.database());

      /**********************************************************************/
      /* Add the Dgn reference to the database                              */
      /**********************************************************************/
      bBTR.appendOdDbEntity(pDgnRef);

      /**********************************************************************/
      /* Set the Dgn reference's parameters                                 */
      /**********************************************************************/
      pDgnRef.setDefinitionId(idDef);
      pDgnRef.setPosition(point.add(new OdGeVector3d(0.0, -h, 0.0)));
      pDgnRef.setScaleFactors(new OdGeScale3d(0.0004));
    }
    void addPdfUnderlay(OdDbObjectId blockId,
                                  int boxRow,
                                  int boxCol,
                                  OdDbObjectId layerId,
                                  OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the BlockTableRecord                                          */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(blockId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the Upper-left corner of the box and its size                  */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Add the label                                                      */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Pdf reference", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);

      /**********************************************************************/
      /* Create the Pdf definition                                          */
      /**********************************************************************/
      OdDbPdfDefinition pPdfDef = OdDbPdfDefinition.createObject();
      String itemName = "1";
      pPdfDef.setSourceFileName("OdWriteEx.pdf");
      pPdfDef.setItemName(itemName);

      // Post to database
      OdDbObjectId idDef = pPdfDef.postDefinitionToDb(blockId.database(),
                                                          "OdWriteEx - " + itemName);

      /**********************************************************************/
      /* Create the Pdf reference                                           */
      /**********************************************************************/
      OdDbPdfReference pPdfRef = OdDbPdfReference.createObject();
      pPdfRef.setDatabaseDefaults(bBTR.database());

      /**********************************************************************/
      /* Add the Pdf reference to the database                              */
      /**********************************************************************/
      bBTR.appendOdDbEntity(pPdfRef);

      /**********************************************************************/
      /* Set the Pdf reference's parameters                                 */
      /**********************************************************************/
      pPdfRef.setDefinitionId(idDef);
      pPdfRef.setPosition(point.add(new OdGeVector3d(0.0, -h, 0.0)));
      pPdfRef.setScaleFactors(new OdGeScale3d(0.2));
    }
    /************************************************************************/
    /* Add some lights to the specified BlockTableRecord                    */
    /************************************************************************/
    void addLights(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);

      /**********************************************************************/
      /* Create a Light                                                     */
      /**********************************************************************/
      OdDbLight pLight = OdDbLight.createObject();
      pLight.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pLight);

      OdGePoint3d ptLight = m_EntityBoxes.getBoxCenter(boxRow, boxCol);
      pLight.setPosition(ptLight);
      pLight.setLightType(OdGiDrawable.DrawableType.kPointLight);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Light", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
    }
    /************************************************************************/
    /* Add some SubDMeshes to the specified BlockTableRecord                    */
    /************************************************************************/
    void addSubDMeshes(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      /**********************************************************************/
      /* Create a SubDMesh                                                     */
      /**********************************************************************/
      OdDbSubDMesh pSubDMesh = OdDbSubDMesh.createObject();
      pSubDMesh.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pSubDMesh);

      OdIntArray[] faceArray = new OdIntArray[1];
      faceArray[0] = new OdIntArray();
      OdGeExtents3d[] ext = new OdGeExtents3d[1];
      ext[0] = new OdGeExtents3d();
      OdGePoint3dArray[] vertexArray = new OdGePoint3dArray[1];
      vertexArray[0] = new OdGePoint3dArray();
      //DbSubDMeshData.set(out vertexArray, out faceArray, out ext);
      DbSubDMeshData.set(vertexArray, faceArray, ext);
      pSubDMesh.setSubDMesh(vertexArray[0], faceArray[0], 0);

      double scaleX = w * 0.7 / (ext[0].maxPoint().getX() - ext[0].minPoint().getX());
      double scaleY = h * 0.7 / (ext[0].maxPoint().getY() - ext[0].minPoint().getY());
      OdGeMatrix3d xfm = OdGeMatrix3d.scaling(Math.min(scaleX, scaleY), ext[0].center());
      pSubDMesh.transformBy(xfm);
      xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext[0].center().asVector()));
      pSubDMesh.transformBy(xfm);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "SubDMesh", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
    }
    /************************************************************************/
    /* Add some ExtrudedSurfaces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addExtrudedSurfaces(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      // base curve 
      OdDbEllipse ellipse = OdDbEllipse.createObject();
      ellipse.set(new OdGePoint3d(0.0, 0.0, 0.0), new OdGeVector3d(0.0, 0.0, 1.0), new OdGeVector3d(2.0, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Create extruded surface                                                     */
      /**********************************************************************/
      try
      {
        OdStreamBuf pFile = TD_Db.odSystemServices().createFile("extrudedsurf.sat");
        OdDbExtrudedSurface pExtruded = OdDbExtrudedSurface.createObject();
        OdDbSweepOptions options = new OdDbSweepOptions();
        pExtruded.createExtrudedSurface(ellipse, new OdGeVector3d(0.0, 1.0, 3.0), options, pFile);
        pExtruded.setDatabaseDefaults(bBTR.database());
        bBTR.appendOdDbEntity(pExtruded);
        OdGeExtents3d ext = new OdGeExtents3d();
        pExtruded.getGeomExtents(ext);
        OdGeMatrix3d xfm = new OdGeMatrix3d();
        xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext.center().asVector()));
        pExtruded.transformBy(xfm);
        double scaleX = w * 0.7 / (ext.maxPoint().getX() - ext.minPoint().getX());
        double scaleY = h * 0.7 / (ext.maxPoint().getY() - ext.minPoint().getY());
        xfm.setToScaling(Math.min(scaleX, scaleY), m_EntityBoxes.getBoxCenter(boxRow, boxCol));
        pExtruded.transformBy(xfm);

        /**********************************************************************/
        /* Add a label                                                        */
        /**********************************************************************/
        addTextEnt(bBTR,
          point.add(m_textOffset), point.add(m_textOffset),
          "Extruded surf", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
      }
      //catch (OdError err)
      catch(Exception err)
      {
        // just skip entity creation
      }
    }
    /************************************************************************/
    /* Add some RevolvedSurfaces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addRevolvedSurfaces(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      // base curve 
      OdDbEllipse ellipse = OdDbEllipse.createObject();
      ellipse.set(new OdGePoint3d(0.0, 0.0, 0.0), new OdGeVector3d(0.0, 0.0, 1.0), new OdGeVector3d(2.0, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Create revolved surface                                                     */
      /**********************************************************************/
      try
      {
        OdStreamBuf pFile = TD_Db.odSystemServices().createFile("revolvedsurf.sat");
        OdDbRevolvedSurface pRevolved = OdDbRevolvedSurface.createObject();
        OdDbRevolveOptions options = new OdDbRevolveOptions();
        pRevolved.createRevolvedSurface(ellipse, new OdGePoint3d(5, 0, 0), new OdGeVector3d(0, 1, 0), 3.14, 0, options, pFile);
        pRevolved.setDatabaseDefaults(bBTR.database());
        bBTR.appendOdDbEntity(pRevolved);
        OdGeExtents3d ext = new OdGeExtents3d();
        pRevolved.getGeomExtents(ext);
        OdGeMatrix3d xfm = new OdGeMatrix3d();
        xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext.center().asVector()));
        pRevolved.transformBy(xfm);
        double scaleX = w * 0.7 / (ext.maxPoint().getX() - ext.minPoint().getX());
        double scaleY = h * 0.7 / (ext.maxPoint().getY() - ext.minPoint().getY());
        xfm.setToScaling(Math.min(scaleX, scaleY), m_EntityBoxes.getBoxCenter(boxRow, boxCol));
        pRevolved.transformBy(xfm);

        /**********************************************************************/
        /* Add a label                                                        */
        /**********************************************************************/
        addTextEnt(bBTR,
          point.add(m_textOffset), point.add(m_textOffset),
          "Revolved surf", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
      }
      //catch (OdError err)
      catch(Exception err)
      {
        // just skip entity creation
      }
    }
    /************************************************************************/
    /* Add some PlaneSurfaces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addPlaneSurfaces(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      // base curve 
      OdDbEllipse ellipse = OdDbEllipse.createObject();
      ellipse.set(new OdGePoint3d(0.0, 0.0, 0.0), new OdGeVector3d(0.0, 0.0, 1.0), new OdGeVector3d(2.0, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Create plane surface                                                     */
      /**********************************************************************/
      OdDbPlaneSurface pPlane = OdDbPlaneSurface.createObject();
      OdRxObjectPtrArray curveSegments = new OdRxObjectPtrArray();
      curveSegments.add(ellipse);
      OdRxObjectPtrArray regions = new OdRxObjectPtrArray();
      OdDbRegion.createFromCurves(curveSegments, regions);
      pPlane.createFromRegion(OdDbRegion.cast(regions.get(0)));
      pPlane.setDatabaseDefaults(bBTR.database());
      bBTR.appendOdDbEntity(pPlane);
      OdGeExtents3d ext = new OdGeExtents3d();
      pPlane.getGeomExtents(ext);
      OdGeMatrix3d xfm = new OdGeMatrix3d();
      xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext.center().asVector()));
      pPlane.transformBy(xfm);
      double scaleX = w * 0.7 / (ext.maxPoint().getX() - ext.minPoint().getX());
      double scaleY = h * 0.7 / (ext.maxPoint().getY() - ext.minPoint().getY());
      xfm.setToScaling(Math.min(scaleX, scaleY), m_EntityBoxes.getBoxCenter(boxRow, boxCol));
      pPlane.transformBy(xfm);

      /**********************************************************************/
      /* Add a label                                                        */
      /**********************************************************************/
      addTextEnt(bBTR,
        point.add(m_textOffset), point.add(m_textOffset),
        "Plane surf", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
    }
    /************************************************************************/
    /* Add some LoftedSurfaces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addLoftedSurfaces(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      // base curve 
      OdDbEllipse ellipse = OdDbEllipse.createObject();
      ellipse.set(new OdGePoint3d(0.0, 0.0, 0.0), new OdGeVector3d(0.0, 0.0, 1.0), new OdGeVector3d(2.0, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Create lofted surface                                                     */
      /**********************************************************************/
      try
      {
        OdStreamBuf pFile = TD_Db.odSystemServices().createFile("loftedsurf.sat");
        OdDbLoftedSurface pLofted = OdDbLoftedSurface.createObject();
        OdDbEntityPtrArray crossSectionCurves = new OdDbEntityPtrArray();
        crossSectionCurves.add(ellipse);
        OdGeMatrix3d mat = new OdGeMatrix3d();
        mat.setToScaling(0.5);
        OdDbEntity e = OdDbEntity.createObject();
        ellipse.getTransformedCopy(mat, e);
        crossSectionCurves.add(e);
        mat.setToTranslation(new OdGeVector3d(0.0, 0.0, 3.0));
        crossSectionCurves.get(1).transformBy(mat);
        OdDbEntityPtrArray guideCurves = new OdDbEntityPtrArray();
        OdDbLoftOptions options = new OdDbLoftOptions();
        pLofted.createLoftedSurface(crossSectionCurves, guideCurves, null, options, pFile);
        pLofted.setDatabaseDefaults(bBTR.database());
        bBTR.appendOdDbEntity(pLofted);
        OdGeExtents3d ext = new OdGeExtents3d();
        pLofted.getGeomExtents(ext);
        OdGeMatrix3d xfm = new OdGeMatrix3d();
        xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext.center().asVector()));
        pLofted.transformBy(xfm);
        double scaleX = w * 0.7 / (ext.maxPoint().getX() - ext.minPoint().getX());
        double scaleY = h * 0.7 / (ext.maxPoint().getY() - ext.minPoint().getY());
        xfm.setToScaling(Math.min(scaleX, scaleY), m_EntityBoxes.getBoxCenter(boxRow, boxCol));
        pLofted.transformBy(xfm);

        /**********************************************************************/
        /* Add a label                                                        */
        /**********************************************************************/
        addTextEnt(bBTR,
          point.add(m_textOffset), point.add(m_textOffset),
          "Lofted surf", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
      }
      //catch (OdError err)
      catch(Exception err)
      {
        // just skip entity creation
      }
    }
    /************************************************************************/
    /* Add some SweptSurfaces to the specified BlockTableRecord                    */
    /************************************************************************/
    void addSweptSurfaces(OdDbObjectId btrId, int boxRow, int boxCol, OdDbObjectId layerId, OdDbObjectId styleId)
    {
      /**********************************************************************/
      /* Open the Block Table Record                                        */
      /**********************************************************************/
      OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));

      /**********************************************************************/
      /* Get the origin and size of the box                                 */
      /**********************************************************************/
      OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
      double w = m_EntityBoxes.getWidth(boxRow, boxCol);
      double h = m_EntityBoxes.getHeight();

      // base curve 
      OdDbEllipse ellipse = OdDbEllipse.createObject();
      ellipse.set(new OdGePoint3d(0.0, 0.0, 0.0), new OdGeVector3d(0.0, 0.0, 1.0), new OdGeVector3d(2.0, 0.0, 0.0), 0.5);

      /**********************************************************************/
      /* Create swept surface                                                     */
      /**********************************************************************/
      try
      {
        OdStreamBuf pFile = TD_Db.odSystemServices().createFile("sweptsurf.sat");
        OdDbSweptSurface pSwept = OdDbSweptSurface.createObject();
        OdDbSweepOptions options = new OdDbSweepOptions();
        OdDb3dPolylineVertex[] aVx = { OdDb3dPolylineVertex.createObject(), OdDb3dPolylineVertex.createObject(), OdDb3dPolylineVertex.createObject() };
        aVx[0].setPosition(new OdGePoint3d(0.0, 0.0, 0.0));
        aVx[1].setPosition(new OdGePoint3d(0.5, 0.0, 2.0));
        aVx[2].setPosition(new OdGePoint3d(-0.5, 0.0, 4.0));
        OdDb3dPolyline poly = OdDb3dPolyline.createObject();
        poly.appendVertex(aVx[0]);
        poly.appendVertex(aVx[1]);
        poly.appendVertex(aVx[2]);
        pSwept.createSweptSurface(ellipse, poly, options, pFile);
        pSwept.setDatabaseDefaults(bBTR.database());
        bBTR.appendOdDbEntity(pSwept);
        OdGeExtents3d ext = new OdGeExtents3d();
        pSwept.getGeomExtents(ext);
        OdGeMatrix3d xfm = new OdGeMatrix3d();
        xfm.setToTranslation(m_EntityBoxes.getBoxCenter(boxRow, boxCol).asVector().subtract(ext.center().asVector()));
        pSwept.transformBy(xfm);
        double scaleX = w * 0.7 / (ext.maxPoint().getX() - ext.minPoint().getX());
        double scaleY = h * 0.7 / (ext.maxPoint().getY() - ext.minPoint().getY());
        xfm.setToScaling(Math.min(scaleX, scaleY), m_EntityBoxes.getBoxCenter(boxRow, boxCol));
        pSwept.transformBy(xfm);

        /**********************************************************************/
        /* Add a label                                                        */
        /**********************************************************************/
        addTextEnt(bBTR,
          point.add(m_textOffset), point.add(m_textOffset),
          "Swept surf", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId);
      }
      //catch (OdError err)
      catch(Exception err)
      {
        // just skip entity creation
      }
    }
    /************************************************************************/
    /* Add a layout                                                         */
    /************************************************************************/
    void addLayout(OdDbDatabase pDb)
    {
      /********************************************************************/
      /* Create a new Layout                                              */
      /********************************************************************/
      OdDbObjectId layoutId = pDb.createLayout("ODA Layout");
      OdDbLayout pLayout = OdDbLayout.cast(layoutId.safeOpenObject());

      /********************************************************************/
      /* Make it current, creating the overall PaperSpace viewport        */
      /********************************************************************/
      pDb.setCurrentLayout(layoutId);

      /********************************************************************/
      /* Open the overall viewport for this layout                        */
      /********************************************************************/
      OdDbViewport pOverallViewport = OdDbViewport.cast(pLayout.overallVportId().safeOpenObject());

      /********************************************************************/
      /* Get some useful parameters                                       */
      /********************************************************************/
      OdGePoint3d overallCenter = pOverallViewport.centerPoint();

      /********************************************************************/
      /* Note:                                                            */
      /* If a viewport is an overall viewport,                            */
      /* the values returned by width() and height() must be divided by a */
      /* factor of 1.058, and the parameters of setWidth and setHeight()  */
      /* must be multiplied a like factor.                                */
      /********************************************************************/
      double margin = 0.25;
      double overallWidth = pOverallViewport.width() / 1.058 - 2 * margin;
      double overallHeight = pOverallViewport.height() / 1.058 - 2 * margin;
      OdGePoint3d overallLLCorner = overallCenter.subtract(new OdGeVector3d(0.5 * overallWidth, 0.5 * overallHeight, 0.0));

      /********************************************************************/
      /* Open the PaperSpace BlockTableRecord for this layout             */
      /********************************************************************/
      OdDbBlockTableRecord pPS = OdDbBlockTableRecord.cast(pLayout.getBlockTableRecordId().safeOpenObject(OpenMode.kForWrite));

      /********************************************************************/
      /* Create a new viewport, and append it to PaperSpace               */
      /********************************************************************/
      OdDbViewport pViewport = OdDbViewport.createObject();
      pViewport.setDatabaseDefaults(pDb);
      pPS.appendOdDbEntity(pViewport);

      /********************************************************************/
      /* Set some parameters                                              */
      /*                                                                  */
      /* This viewport occupies the upper half of the overall viewport,   */
      /* and displays all objects in model space                          */
      /********************************************************************/

      pViewport.setWidth(overallWidth);
      pViewport.setHeight(overallHeight * 0.5);
      pViewport.setCenterPoint(overallCenter.add(new OdGeVector3d(0.0, 0.5 * pViewport.height(), 0.0)));
      pViewport.setViewCenter(pOverallViewport.viewCenter());
      pViewport.zoomExtents();

      /********************************************************************/
      /* Create viewports for each of the entities that have been         */
      /* pushBacked onto m_layoutEntities                                 */
      /********************************************************************/
      if (m_layoutEntities.size() != 0)
      {
        double widthFactor = 1.0 / m_layoutEntities.size();
        for (int i = 0; i < m_layoutEntities.size(); ++i)
        {
          OdDbEntity pEnt = OdDbEntity.cast(m_layoutEntities.get(i).safeOpenObject());
          OdGeExtents3d entityExtents = new OdGeExtents3d();
          if (pEnt.getGeomExtents(entityExtents) == OdResult.eOk)
          {
            /**************************************************************/
            /* Create a new viewport, and append it to PaperSpace         */
            /**************************************************************/
            pViewport = OdDbViewport.createObject();
            pViewport.setDatabaseDefaults(pDb);
            pPS.appendOdDbEntity(pViewport);

            /**************************************************************/
            /* The viewports are tiled along the bottom of the overall    */
            /* viewport                                                   */
            /**************************************************************/
            pViewport.setWidth(overallWidth * widthFactor);
            pViewport.setHeight(overallHeight * 0.5);
            pViewport.setCenterPoint(overallLLCorner.add(new OdGeVector3d((i + 0.5) * pViewport.width(), 0.5 * pViewport.height(), 0.0)));

            /**************************************************************/
            /* The target of the viewport is the midpoint of the entity   */
            /* extents                                                    */
            /**************************************************************/
            OdGePoint3d minPt = entityExtents.minPoint();
            OdGePoint3d maxPt = entityExtents.maxPoint();
            OdGePoint3d viewTarget = new OdGePoint3d();
            viewTarget.setX((minPt.getX() + maxPt.getX()) / 2.0);
            viewTarget.setY((minPt.getY() + maxPt.getY()) / 2.0);
            viewTarget.setZ((minPt.getZ() + maxPt.getZ()) / 2.0);
            pViewport.setViewTarget(viewTarget);

            /**************************************************************/
            /* The viewHeight is the larger of the height as defined by   */
            /* the entityExtents, and the height required to display the  */
            /* width of the entityExtents                                 */
            /**************************************************************/
            double viewHeight = Math.abs(maxPt.getY() - minPt.getY());
            double viewWidth = Math.abs(maxPt.getX() - minPt.getX());
            viewHeight = Math.max(viewHeight, viewWidth * pViewport.height() / pViewport.width());
            pViewport.setViewHeight(viewHeight * 1.05);
          }
        }
      }
      pDb.setTILEMODE(true); // Disable PaperSpace
    }

///////////////////////////////////////////////////////////
/////////////// Big method adding stop ////////////////////
///////////////////////////////////////////////////////////
	  
	public void fillDatabase(OdDbDatabase db) {
	  /**********************************************************************/
	  /* Set Creation and Last Update times                                 */
	  /**********************************************************************/
	  OdDbDate date = new OdDbDate();

	  date.setDate((short)1, (short)1, (short)2006);
	  date.setTime((short)12, (short)0, (short)0, (short)0); 
	  date.localToUniversal();
	  TD_Db.odDbSetTDUCREATE(db, date);

	  date.getUniversalTime();
	  TD_Db.odDbSetTDUUPDATE(db, date);

	  /**********************************************************************/
	  /* Add some Registered Applications                                   */
	  /**********************************************************************/
	  addRegApp(db, "ODA");
	  addRegApp(db, "AVE_FINISH"); // for materials
	  /**********************************************************************/
	  /* Add an SHX text style                                              */
	  /**********************************************************************/
    OdDbObjectId shxTextStyleId = addStyle(db, "OdaShxStyle", 0.0, 1.0, 0.2, 0.0, "txt", false, "", false, false, 0, 0);

	  /**********************************************************************/
	  /* Add a TTF text style                                               */
	  /**********************************************************************/
	  OdDbObjectId ttfStyleId = addStyle(db, "OdaTtfStyle", 0.0, 1.0, 0.2, 0.0, 
		  "VERDANA.TTF", false, "Verdana", false, false, 0, 34);
	  
	  /**********************************************************************/
	  /* Add a Shape file style for complex linetypes                       */
	  /**********************************************************************/
    OdDbObjectId shapeStyleId = addStyle(db, "", 0.0, 1.0, 0.2, 0.0, "ltypeshp.shx", true, "", false, false, 0, 0);

	  /**********************************************************************/
	  /* Add some linetypes                                                 */
	  /**********************************************************************/  
	  addLinetypes(db, shapeStyleId, shxTextStyleId);

	  /**********************************************************************/
	  /* Add a Layer                                                        */
	  /**********************************************************************/
	  OdDbObjectId odaLayer1Id = addLayer(db, "Oda Layer 1", OdCmEntityColor.ACIcolorMethod.kACIRed, "CONTINUOUS");

	  /**********************************************************************/
	  /* Add a block definition                                             */
	  /**********************************************************************/ 
	  OdDbObjectId odaBlock1Id = addBlockDef(db, "OdaBlock1", 1, 2); 

	  /**********************************************************************/
	  /* Add a DimensionStyle                                               */
	  /**********************************************************************/ 
	  OdDbObjectId odaDimStyleId = addDimStyle(db, "OdaDimStyle"); 

	  /**********************************************************************/
	  /* Add an MLine style                                                 */
	  /**********************************************************************/ 
	  OdDbObjectId odaMLineStyleId = addMLineStyle(db, "OdaStandard", "ODA Standard Style"); 

	  /**********************************************************************/
	  /* Add a Material                                                     */
	  /**********************************************************************/ 
	  OdDbObjectId odaMaterialId = addMaterial(db, "OdaMaterial", "ODA Defined Material"); 
	  
	  /**********************************************************************/
	  /* Add a PaperSpace Viewport                                          */
	  /**********************************************************************/
	  addPsViewport(db, odaLayer1Id);

	  /**********************************************************************/
	  /* Add ModelSpace Entity Boxes                                        */
	  /**********************************************************************/  
	  createEntityBoxes(db.getModelSpaceId(), odaLayer1Id);

	  /**********************************************************************/
	  /* Add some lines                                                     */
	  /**********************************************************************/  
	  addLines(db.getModelSpaceId(), 0, 0, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a 2D (heavy) polyline                                          */
	  /**********************************************************************/  
	  add2dPolyline(db.getModelSpaceId(), 0, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a PolyFace Mesh                                                */
	  /**********************************************************************/  
	  addPolyFaceMesh(db.getModelSpaceId(), 0, 2, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a PolygonMesh                                                */
	  /**********************************************************************/  
	  addPolygonMesh(db.getModelSpaceId(), 0, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some curves                                                    */
	  /**********************************************************************/  
	  addCurves(db.getModelSpaceId(), 0, 4, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Tolerance                                                    */
	  /**********************************************************************/  
	  addTolerance(db.getModelSpaceId(), 0, 5, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some Leaders                                                   */
	  /**********************************************************************/  
	  //addLeaders(db.getModelSpaceId(), 0, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Aligned Dimension                                           */
	  /**********************************************************************/  
	  addAlignedDimension(db.getModelSpaceId(), 0, 7, odaLayer1Id, ttfStyleId, odaDimStyleId);

	  /**********************************************************************/
	  /* Add a MultiLine                                                    */
	  /**********************************************************************/  
	  addMLine(db.getModelSpaceId(), 0, 8, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Arc Dimension                                               */
	  /**********************************************************************/  
	  addArcDimension(db.getModelSpaceId(), 0, 9, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a 3D Polyline                                                  */
	  /**********************************************************************/  
	  add3dPolyline(db.getModelSpaceId(), 1, 0, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add MText                                                          */
	  /**********************************************************************/  
	  addMText(db.getModelSpaceId(), 1, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Block Reference                                                */
	  /**********************************************************************/  
	  //addBlockRef(db.getModelSpaceId(), 1, 2, odaLayer1Id, ttfStyleId, odaBlock1Id);
	  
	  /**********************************************************************/
	  /* Add Radial Dimension                                               */
	  /**********************************************************************/  
	  addRadialDimension(db.getModelSpaceId(), 1, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add 3D Face                                                       */
	  /**********************************************************************/  
	  add3dFace(db.getModelSpaceId(), 1, 4, odaLayer1Id, ttfStyleId);
	  
	  /**********************************************************************/
	  /* Add RText                                                          */
	  /**********************************************************************/  
	  addRText(db.getModelSpaceId(), 1, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Hatches                                                          */
	  /**********************************************************************/  
	  addHatches(db.getModelSpaceId(), 2, 0, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some text entities to ModelSpace                               */
	  /**********************************************************************/  
	  addTextEnts(db.getModelSpaceId(), 2, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Solid                                                          */
	  /**********************************************************************/  
	  addSolid(db.getModelSpaceId(), 2, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Associative Dimension                                       */
	  /**********************************************************************/  
	  addDimAssoc(db.getModelSpaceId(), 2, 4, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Ray                                       */
	  /**********************************************************************/  
	  addRay(db.getModelSpaceId(), 3, 0, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a 3 Point Angular Dimension                                       */
	  /**********************************************************************/  
	  add3PointAngularDimension(db.getModelSpaceId(), 3, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Ordinate Dimensions                                            */
	  /**********************************************************************/  
	  addOrdinateDimensions(db.getModelSpaceId(), 3, 2, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Spline                                                       */
	  /**********************************************************************/  
	  addSpline(db.getModelSpaceId(), 3, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some Traces                                                    */
	  /**********************************************************************/  
	  addTraces(db.getModelSpaceId(), 3, 4, odaLayer1Id, ttfStyleId);
	  
	  /**********************************************************************/
	  /* Add a Polyline                                                     */
	  /**********************************************************************/  
	  addPolyline(db.getModelSpaceId(), 3, 5, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an ArcAlignedText                                              */
	  /**********************************************************************/  
	  addArcText(db.getModelSpaceId(), 3, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Wipeout                                                      */
	  /**********************************************************************/  
	  addWipeout(db.getModelSpaceId(), 3, 7, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a RadialDimensionLarge                                         */
	  /**********************************************************************/  
	  addRadialDimensionLarge(db.getModelSpaceId(), 3, 8, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a 2 Line Angular Dimension                                       */
	  /**********************************************************************/  
	  add2LineAngularDimension(db.getModelSpaceId(), 3, 9, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an ACIS Solid                                                  */
	  /**********************************************************************/  
	  addACIS(db.getModelSpaceId(), 1, 5, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Image                                                       */
	  /**********************************************************************/  
	  addImage(db.getModelSpaceId(), 4, 0, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add an Xref                                                        */
	  /**********************************************************************/  
	  addXRef(db.getModelSpaceId(), 4, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Table                                                        */
	  /**********************************************************************/  
	  addTable(db.getModelSpaceId(), odaBlock1Id, 4, 2, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Diametric Dimension                                               */
	  /**********************************************************************/  
	  addDiametricDimension(db.getModelSpaceId(), 4, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Shape                                                        */
	  /**********************************************************************/  
	  addShape(db.getModelSpaceId(), 4, 4, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a MInsert                                                      */
	  /**********************************************************************/  
	  addMInsert(db.getModelSpaceId(), 4, 5, odaLayer1Id, ttfStyleId, odaBlock1Id);

	  /**********************************************************************/
	  /* Add an Xline                                                       */
	  /**********************************************************************/  
	  addXline(db.getModelSpaceId(), 4, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add custom objects                                                 */
	  /**********************************************************************/  
	  addCustomObjects(db);

	  /**********************************************************************/
	  /* Add Text with Field                                                */
	  /**********************************************************************/  
	  addTextWithField(db.getModelSpaceId(), 5, 0, odaLayer1Id, shxTextStyleId, ttfStyleId);

	  /**********************************************************************/
	  /* Add OLE object                                                     */
	  /**********************************************************************/  
	  addOLE2FrameFromFile(db.getModelSpaceId(), 5, 1, "OdWriteEx.xls", odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Box                                                            */
	  /**********************************************************************/  
	  addBox(db.getModelSpaceId(), 5, 2, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Frustum                                                        */
	  /**********************************************************************/  
	  addFrustum(db.getModelSpaceId(), 5, 3, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Sphere                                                         */
	  /**********************************************************************/  
	  addSphere(db.getModelSpaceId(), 5, 4, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Torus                                                          */
	  /**********************************************************************/  
	  addTorus(db.getModelSpaceId(), 5, 5, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Wedge                                                          */
	  /**********************************************************************/  
	  addWedge(db.getModelSpaceId(), 5, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Region                                                         */
	  /**********************************************************************/  
	  addRegion(db.getModelSpaceId(), 5, 7, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Helix                                                          */
	  /**********************************************************************/  
	  addHelix(db.getModelSpaceId(), 6, 2, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add Underlays                                                   */
	  /**********************************************************************/  
	  addDwfUnderlay(db.getModelSpaceId(), 6, 3, odaLayer1Id, ttfStyleId);
	  addDgnUnderlay(db.getModelSpaceId(), 6, 4, odaLayer1Id, ttfStyleId);
	  addPdfUnderlay(db.getModelSpaceId(), 6, 5, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some MLeaders                                                  */
	  /**********************************************************************/  
	 // addMLeaders(db.getModelSpaceId(), 7, 4, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some Lights                                                  */
	  /**********************************************************************/  
	  addLights(db.getModelSpaceId(), 6, 6, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some SubDMeshes                                                  */
	  /**********************************************************************/  
	  addSubDMeshes(db.getModelSpaceId(), 6, 7, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add some Surfaces                                                  */
	  /**********************************************************************/  
	  addExtrudedSurfaces(db.getModelSpaceId(), 6, 8, odaLayer1Id, ttfStyleId);
	  addRevolvedSurfaces(db.getModelSpaceId(), 6, 9, odaLayer1Id, ttfStyleId);
	  // commented for now as OdDbRegion.createFromCurves is not wrapped properly
	  //addPlaneSurfaces(db.getModelSpaceId(), 6, 10, odaLayer1Id, ttfStyleId);
	  //addLoftedSurfaces(db.getModelSpaceId(), 7, 0, odaLayer1Id, ttfStyleId);
	  addSweptSurfaces(db.getModelSpaceId(), 7, 1, odaLayer1Id, ttfStyleId);

	  /**********************************************************************/
	  /* Add a Layout                                                       */
	  /**********************************************************************/  
	  addLayout(db);


	  // If preview bitmap is already available it can be specified to avoid wasting
	  // time on generating it by DD
	/*  const OdChar* pBmpFileName = "preview.bmp";
	  if(::odSystemServices()->accessFile(pBmpFileName, Oda::kFileRead))
	  {
		OdRdFileBuf bmpFile(pBmpFileName);

		OdArray<short> buf;
		buf.resize((short)bmpFile.length());
		short * pData = buf.asArrayPtr(); 
		bmpFile.getBytes(pData, buf.length());
		// Get length taking care about big-endian
		long length = pData[2] + (pData[3] << 8);
		pData += 14;  // Skip BITMAPFILEHEADER
		pDb.setThumbnailBitmap(pData, length);
	  }*/
	  
	} // fillDatabase END
	
	/************************************************************************/
	/* Add a Text Style to the specified database                           */
	/*                                                                      */
	/* The symbol table and symbol table record are implicitly closed when  */
	/* this function returns.                                               */
	/************************************************************************/
	OdDbObjectId addStyle(OdDbDatabase db,
                        String styleName,
                        double textSize,
                        double xScale,
                        double priorSize,
                        double obliquing,
                        String fileName,
                        boolean isShapeFile,
                        String ttFaceName,
                        boolean bold,
                        boolean italic,
                        int charset,
                        int pitchAndFamily)
	{
	  OdDbObjectId styleId;

	  OdDbTextStyleTable styles = OdDbTextStyleTable.cast(db.getTextStyleTableId().safeOpenObject(OpenMode.kForWrite));
	  OdDbTextStyleTableRecord style = OdDbTextStyleTableRecord.createObject();

	  // Name must be set before a table object is added to a table.  The
	  // isShapeFile flag must also be set (if true) before adding the object
	  // to the database.
	  style.setName(styleName);
	  style.setIsShapeFile(isShapeFile);

	  // Add the object to the table.
	  styleId = styles.add(style);

	  // Set the remaining properties.
	  style.setTextSize(textSize);
	  style.setXScale(xScale);
	  style.setPriorSize(priorSize);
	  style.setObliquingAngle(obliquing);
	  style.setFileName(fileName);
	  if (isShapeFile)
	  {
	    style.setPriorSize(22.45);
	  }
	  if (!ttFaceName.isEmpty())
	  {
	    style.setFont(ttFaceName, bold, italic, charset, pitchAndFamily);
	  }

	  return styleId;
	}
	
	/************************************************************************/
	/* Add a Layer to the specified database                                */
	/*                                                                      */
	/* The symbol table and symbol table record are implicitly closed when  */
	/* this function returns.                                               */
	/************************************************************************/
	OdDbObjectId addLayer(OdDbDatabase db, String name, OdCmEntityColor.ACIcolorMethod color, String linetype)
	{
	  /**********************************************************************/
	  /* Open the layer table                                               */
	  /**********************************************************************/
	  OdDbLayerTable layers = OdDbLayerTable.cast(db.getLayerTableId().safeOpenObject(OpenMode.kForWrite));

	  /**********************************************************************/
	  /* Create a layer table record                                        */
	  /**********************************************************************/
	  OdDbLayerTableRecord layer = OdDbLayerTableRecord.createObject();

	  /**********************************************************************/
	  /* Layer must have a name before adding it to the table.              */
	  /**********************************************************************/
	  layer.setName(name);

	  /**********************************************************************/
	  /* Set the Color.                                                     */
	  /**********************************************************************/
	  layer.setColorIndex((short)color.swigValue());

	  /**********************************************************************/
	  /* Set the Linetype.                                                  */
	  /**********************************************************************/
	  OdDbLinetypeTable linetypes = OdDbLinetypeTable.cast(db.getLinetypeTableId().safeOpenObject(OpenMode.kForRead));
	  OdDbObjectId linetypeId = linetypes.getAt(linetype);
	  layer.setLinetypeObjectId(linetypeId);

	  /**********************************************************************/
	  /* Add the record to the table.                                       */
	  /**********************************************************************/
	  OdDbObjectId layerId = layers.add(layer);

	  return layerId;
	} 
  
	/************************************************************************/
	/* Add some lines to the specified BlockTableRecord                     */
	/************************************************************************/
	void addLines(OdDbObjectId btrId,
	              int boxRow,
	              int boxCol,
	              OdDbObjectId layerId,
	              OdDbObjectId styleId)
	{

	  /**********************************************************************/
	  /* Open the Block Table Record                                        */
	  /**********************************************************************/
	  OdDbBlockTableRecord bBTR = OdDbBlockTableRecord.cast(btrId.safeOpenObject(OpenMode.kForWrite));
	  
	  /**********************************************************************/
	  /* Get the origin and size of the box                                 */
	  /**********************************************************************/
	  OdGePoint3d point = m_EntityBoxes.getBox(boxRow, boxCol);
	//  double      w     = m_EntityBoxes.getWidth(boxRow, boxCol);
	//  double      h     = m_EntityBoxes.getHeight();

	  /**********************************************************************/
	  /* Add a label                                                        */
	  /**********************************************************************/
	  addTextEnt(bBTR, point.add(m_textOffset), point.add(m_textOffset),
	      "LINEs", m_textSize, TextHorzMode.kTextLeft, TextVertMode.kTextTop, layerId, styleId, null );

	  
	  
	  /**********************************************************************/
	  /* Get the center of the box                                          */
	  /**********************************************************************/
	  point = m_EntityBoxes.getBoxCenter(0,0);
	  
	  /**********************************************************************/
	  /* Add the lines that describe a 12 pointed star                      */
	  /**********************************************************************/
	  OdGeVector3d toStart = new OdGeVector3d(1.0, 0.0, 0.0);

	  for (int i = 0; i < 12; i++)
	  {
	    OdDbLine line = OdDbLine.createObject();
	    line.setDatabaseDefaults(bBTR.database());
	    bBTR.appendOdDbEntity(line);
	    line.setStartPoint(point.add(toStart));
	    line.setEndPoint(point.add(toStart.rotateBy((160.0)*Math.PI/180.0, OdGeVector3d.getKZAxis())));
	  }
	}
  }   //DbFiller CLASS END
  
  
  private static class EntityBoxes
  {
	  final  static  double WIDTH_BOX =  2.25;
	  final  static  double HEIGHT_BOX = 3.25;

	  final  static  double HOR_SPACE =  0.625;
	  final  static  double VER_SPACE =  0.375;

	  final  static  int  HOR_BOXES  = 11;
	  final  static  int  VER_BOXES  = 8;
 
      static int BoxSizes[][] = {
    	  {1,1,1,1,2,1,1,1,1,1,0},
    	  {1,3,2,1,1,1,2,0,0,0,0},
    	  {2,3,3,1,2,0,0,0,0,0,0},
    	  {1,1,1,2,1,1,1,1,1,1,0},
    	  {2,2,2,1,1,2,1,0,0,0,0},
    	  {3,2,1,1,1,1,1,1,0,0,0},
    	  {1,1,1,1,1,1,1,1,1,1,1},
    	  {1,1,1,1,1,1,1,1,1,1,1}
    	};
    
  
      public EntityBoxes(){}
  

    /**********************************************************************/
    /* Return the width of the specified box                              */
    /**********************************************************************/
    double getWidth(int row, int col) 
    {
      return BoxSizes[row][col]*WIDTH_BOX + (BoxSizes[row][col] - 1 )*HOR_SPACE;
    }
    /**********************************************************************/
    /* Return the height of specified box                                 */
    /**********************************************************************/
    double getHeight() 
    {
      return HEIGHT_BOX;
    }
    /**********************************************************************/
    /* Return true if and only if the specified box is a box              */
    /**********************************************************************/
    boolean isBox(int row, int col)
    {
      return BoxSizes[row][col] > 0 ? true : false;
    }
    
    /**********************************************************************/
    /* Return the upper-left corner of the specified box                  */
    /**********************************************************************/
    OdGePoint3d getBox(int row, int col)
    {
      OdGePoint3d point = new OdGePoint3d();
      if ( col > HOR_BOXES-1 )
        return point;

      point = new OdGePoint3d(0.0, HEIGHT_BOX * VER_BOXES + VER_SPACE * (VER_BOXES-1), 0.0);
      
      for (int i=0; i < col;  i++ )
      {
        point.setX( point.getX() + BoxSizes[row][i]*WIDTH_BOX );
        point.setX( point.getX() + (BoxSizes[row][i])*HOR_SPACE);;
      }
      point.setY( point.getY() - row*HEIGHT_BOX);
      point.setY( point.getY() - row*VER_SPACE);
      return point;
    }

    /**********************************************************************/
    /* Return the center of the specified box                             */
    /**********************************************************************/
    OdGePoint3d getBoxCenter(int row, int col) 
    {
      OdGePoint3d point = getBox(row,col);
      double w = getWidth(row,col);
      point.setX( point.getX() + w/2.0);
      point.setY( point.getY() - HEIGHT_BOX/2.0);
      return point;
    }

    /**********************************************************************/
    /* Return the size of the box array                                   */
    /**********************************************************************/
    OdGeVector3d getArraySize() 
    {
      return new OdGeVector3d(WIDTH_BOX * HOR_BOXES + HOR_SPACE * (HOR_BOXES-1), 
                         -(HEIGHT_BOX * VER_BOXES + VER_SPACE * (VER_BOXES-1)),
                         0.0);
    }

    /**********************************************************************/
    /* Return the center of the box array                                 */
    /**********************************************************************/
    OdGePoint3d getArrayCenter() 
    {
      return new OdGePoint3d(0.5 * (WIDTH_BOX * HOR_BOXES + HOR_SPACE * (HOR_BOXES-1)), 
    		  				 0.5 * (HEIGHT_BOX * VER_BOXES + VER_SPACE * (VER_BOXES-1)),
                              0.0);
    }
  } // Entity Boxes CLASS
  
}

  class DbSubDMeshData
  {
    // see attachment for http://bugzilla.opendesign.com/show_bug.cgi?id=7174
    public static void set(OdGePoint3dArray[] vertexArray, OdIntArray[] faceArray, OdGeExtents3d[] ext)
    {
      vertexArray[0] = OdGePoint3dArray.repeat(OdGePoint3d.getKOrigin(), 348);
      // fill vertices
      vertexArray[0].get(0).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(1).set(2.8501694202423096, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(2).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(3).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(4).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(5).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(6).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(7).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(8).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(9).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(10).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(11).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(12).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(13).set(0.33048447966575623, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(14).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(15).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(16).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(17).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(18).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(19).set(0.33048447966575623, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(20).set(0.33048447966575623, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(21).set(2.8501694202423096, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(22).set(0.33048447966575623, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(23).set(0.33048447966575623, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(24).set(2.8501694202423096, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(25).set(0.33048447966575623, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(26).set(0.33048447966575623, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(27).set(2.8501694202423096, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(28).set(0.33048447966575623, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(29).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(30).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(31).set(2.8501694202423096, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(32).set(2.8501694202423096, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(33).set(0.33048447966575623, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(34).set(2.8501694202423096, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(35).set(2.8501694202423096, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(36).set(0.33048447966575623, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(37).set(2.8501694202423096, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(38).set(2.8501694202423096, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(39).set(0.33048447966575623, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(40).set(2.8501694202423096, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(41).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(42).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(43).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(44).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(45).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(46).set(2.8501694202423096, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(47).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(48).set(2.8501694202423096, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(49).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(50).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(51).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(52).set(0.33048447966575623, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(53).set(2.8501694202423096, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(54).set(2.8501694202423096, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(55).set(2.8501694202423096, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(56).set(0.33048447966575623, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(57).set(0.33048447966575623, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(58).set(0.33048447966575623, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(59).set(2.8501694202423096, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(60).set(2.8501694202423096, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(61).set(0.33048447966575623, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(62).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(63).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(64).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(65).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(66).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(67).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(68).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(69).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(70).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(71).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(72).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(73).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(74).set(0.33048447966575623, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(75).set(2.8501694202423096, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(76).set(0.33048447966575623, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(77).set(0.33048447966575623, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(78).set(2.8501694202423096, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(79).set(0.33048447966575623, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(80).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(81).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(82).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(83).set(0.33048447966575623, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(84).set(2.8501694202423096, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(85).set(0.33048447966575623, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(86).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(87).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(88).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(89).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(90).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(91).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(92).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(93).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(94).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(95).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(96).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(97).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(98).set(0.33048447966575623, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(99).set(0.33048447966575623, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(100).set(2.8501694202423096, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(101).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(102).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(103).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(104).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(105).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(106).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(107).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(108).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(109).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(110).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(111).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(112).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(113).set(2.8501694202423096, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(114).set(0.33048447966575623, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(115).set(2.8501694202423096, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(116).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(117).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(118).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(119).set(2.8501694202423096, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(120).set(0.33048447966575623, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(121).set(2.8501694202423096, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(122).set(2.8501694202423096, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(123).set(0.33048447966575623, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(124).set(2.8501694202423096, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(125).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(126).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(127).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(128).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(129).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(130).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(131).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(132).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(133).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(134).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(135).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(136).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(137).set(2.8501694202423096, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(138).set(2.8501694202423096, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(139).set(2.8501694202423096, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(140).set(0.33048447966575623, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(141).set(0.33048447966575623, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(142).set(0.33048447966575623, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(143).set(2.8501694202423096, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(144).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(145).set(2.8501694202423096, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(146).set(2.8501694202423096, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(147).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(148).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(149).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(150).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(151).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(152).set(2.8501694202423096, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(153).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(154).set(2.8501694202423096, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(155).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(156).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(157).set(2.8501694202423096, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(158).set(2.8501694202423096, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(159).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(160).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(161).set(2.8501694202423096, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(162).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(163).set(2.8501694202423096, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(164).set(2.8501694202423096, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(165).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(166).set(2.8501694202423096, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(167).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(168).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(169).set(2.8501694202423096, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(170).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(171).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(172).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(173).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(174).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(175).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(176).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(177).set(2.8501694202423096, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(178).set(2.8501694202423096, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(179).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(180).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(181).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(182).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(183).set(2.8501694202423096, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(184).set(2.8501694202423096, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(185).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(186).set(2.8501694202423096, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(187).set(2.8501694202423096, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(188).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(189).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(190).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(191).set(2.8501694202423096, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(192).set(2.8501694202423096, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(193).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(194).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(195).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(196).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(197).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(198).set(2.8501694202423096, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(199).set(2.8501694202423096, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(200).set(2.8501694202423096, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(201).set(2.8501694202423096, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(202).set(2.8501694202423096, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(203).set(2.8501694202423096, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(204).set(2.8501694202423096, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(205).set(2.8501694202423096, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(206).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(207).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(208).set(2.8501694202423096, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(209).set(2.8501694202423096, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(210).set(2.8501694202423096, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(211).set(2.8501694202423096, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(212).set(2.8501694202423096, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(213).set(2.8501694202423096, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(214).set(2.8501694202423096, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(215).set(2.8501694202423096, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(216).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(217).set(2.8501694202423096, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(218).set(2.8501694202423096, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(219).set(2.8501694202423096, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(220).set(2.8501694202423096, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(221).set(2.8501694202423096, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(222).set(2.8501694202423096, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(223).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(224).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(225).set(2.8501694202423096, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(226).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(227).set(2.8501694202423096, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(228).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(229).set(0.33048447966575623, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(230).set(0.33048447966575623, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(231).set(0.33048447966575623, -1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(232).set(0.33048447966575623, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(233).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(234).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(235).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(236).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(237).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(238).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(239).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(240).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(241).set(0.33048447966575623, -0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(242).set(0.33048447966575623, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(243).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(244).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(245).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(246).set(0.33048447966575623, -1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(247).set(0.33048447966575623, -1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(248).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(249).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(250).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(251).set(0.33048447966575623, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(252).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(253).set(0.33048447966575623, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(254).set(0.33048447966575623, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(255).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(256).set(0.33048447966575623, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(257).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(258).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(259).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(260).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(261).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(262).set(0.33048447966575623, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(263).set(0.33048447966575623, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(264).set(0.33048447966575623, -0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(265).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(266).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(267).set(0.33048447966575623, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(268).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(269).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(270).set(0.33048447966575623, 0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(271).set(0.33048447966575623, -0.10427964478731155, 0.54523563385009766);
      vertexArray[0].get(272).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(273).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(274).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(275).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(276).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(277).set(0.33048447966575623, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(278).set(0.33048447966575623, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(279).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(280).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(281).set(0.33048447966575623, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(282).set(0.33048447966575623, -1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(283).set(0.33048447966575623, -1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(284).set(0.33048447966575623, -0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(285).set(0.33048447966575623, 0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(286).set(0.33048447966575623, 0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(287).set(0.33048447966575623, 1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(288).set(0.33048447966575623, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(289).set(0.33048447966575623, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(290).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(291).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(292).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(293).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(294).set(0.33048447966575623, -0.29811960458755493, 0.46827429533004761);
      vertexArray[0].get(295).set(0.33048447966575623, -0.44987928867340088, 0.32521492242813110);
      vertexArray[0].get(296).set(0.33048447966575623, -1.6141731739044189, 1.1811023950576782);
      vertexArray[0].get(297).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(298).set(0.33048447966575623, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(299).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(300).set(0.33048447966575623, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(301).set(0.33048447966575623, 0.55043613910675049, -0.071945667266845703);
      vertexArray[0].get(302).set(0.33048447966575623, 0.53813737630844116, 0.13625068962574005);
      vertexArray[0].get(303).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(304).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(305).set(0.33048447966575623, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(306).set(0.33048447966575623, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(307).set(0.33048447966575623, 0.35117831826210022, -0.42991846799850464);
      vertexArray[0].get(308).set(0.33048447966575623, 0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(309).set(0.33048447966575623, -0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(310).set(0.33048447966575623, -1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(311).set(0.33048447966575623, -0.48503947257995605, -0.26998671889305115);
      vertexArray[0].get(312).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(313).set(2.8501694202423096, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(314).set(0.33048447966575623, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(315).set(0.33048447966575623, 0.70866143703460693, -0.78740155696868896);
      vertexArray[0].get(316).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(317).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(318).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(319).set(2.8501694202423096, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(320).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(321).set(0.33048447966575623, 1.2598425149917603, -0.78740155696868896);
      vertexArray[0].get(322).set(0.33048447966575623, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(323).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(324).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(325).set(2.8501694202423096, 1.6141731739044189, 0.39370077848434448);
      vertexArray[0].get(326).set(2.8501694202423096, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(327).set(0.33048447966575623, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(328).set(2.8501694202423096, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(329).set(2.8501694202423096, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(330).set(0.33048447966575623, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(331).set(2.8501694202423096, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(332).set(2.8501694202423096, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(333).set(0.33048447966575623, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(334).set(2.8501694202423096, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(335).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(336).set(2.8501694202423096, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(337).set(0.33048447966575623, 1.2598425149917603, -0.063738189637660980);
      vertexArray[0].get(338).set(0.33048447966575623, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(339).set(2.8501694202423096, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(340).set(0.33048447966575623, 1.2852622270584106, 0.089142479002475739);
      vertexArray[0].get(341).set(0.33048447966575623, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(342).set(2.8501694202423096, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(343).set(0.33048447966575623, 1.3587861061096191, 0.22557161748409271);
      vertexArray[0].get(344).set(0.33048447966575623, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(345).set(2.8501694202423096, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(346).set(0.33048447966575623, 1.4725021123886108, 0.33086803555488586);
      vertexArray[0].get(347).set(0.33048447966575623, 1.6141731739044189, 0.39370077848434448);

      // find bbox
      ext[0] = new OdGeExtents3d();
      for (int i = 0; i < vertexArray[0].size(); ++i)
      {
        ext[0].addPoint(vertexArray[0].get(i));
      }

      // fill faces
      faceArray[0] = OdIntArray.repeat(0, 464);
      int Ctr = 0;
      for (int i = 0; i < 464; i++)
      {
        if ((i % 4) == 0)
        {
          faceArray[0].set(i, 3);
        }
        else
        {
          faceArray[0].set(i, Ctr++);
        }
      }
    }
  }