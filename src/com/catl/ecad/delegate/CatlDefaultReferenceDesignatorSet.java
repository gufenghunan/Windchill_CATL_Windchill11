package com.catl.ecad.delegate;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import wt.inf.container.WTContainerHelper;
import wt.org.WTUser;
import wt.part.DefaultReferenceDesignatorSet;
import wt.part.ReferenceDesignatorSet;
import wt.preference.PreferenceHelper;
import wt.util.WTException;
import wt.util.WTProperties;


public class CatlDefaultReferenceDesignatorSet extends DefaultReferenceDesignatorSet {
	private static final String RESOURCE = "wt.part.partResource";
	private List expandedReferenceDesignators;
	private String consolidatedReferenceDesignators;

	@Override
	public ReferenceDesignatorSet newReferenceDesignatorSet() throws WTException {
		return new CatlDefaultReferenceDesignatorSet();
	}
	@Override
	public void initialize(List arg0) throws WTException {
		this.expandedReferenceDesignators = new ArrayList(arg0);
		this.sortReferenceDesignators();
	}
	@Override
	public void initialize(String arg0) throws WTException {
		this.consolidatedReferenceDesignators = arg0;
		this.sortReferenceDesignators();
	}
	@Override
	public List getExpandedReferenceDesignators() throws WTException {
		if (this.expandedReferenceDesignators == null) {
			this.calculateExpandedReferenceDesignators();
		}

		return this.expandedReferenceDesignators;
	}
	
	@Override
	public String getConsolidatedReferenceDesignators() throws WTException {
		if (this.consolidatedReferenceDesignators == null) {
			this.calculateConsolidatedReferenceDesignators(false);
		}

		return this.consolidatedReferenceDesignators;
	}

	@Override
	public String getConsolidatedReferenceDesignatorsForEditing() throws WTException {
		if (this.consolidatedReferenceDesignators == null) {
			this.calculateConsolidatedReferenceDesignators(true);
		}

		return this.consolidatedReferenceDesignators;
	}

	private void sortReferenceDesignators() throws WTException {
		if (this.consolidatedReferenceDesignators != null && this.expandedReferenceDesignators == null) {
			this.calculateExpandedReferenceDesignators();
		}

		this.consolidatedReferenceDesignators = null;
		int arg0 = this.expandedReferenceDesignators.size();
		CatlDefaultReferenceDesignatorSet.ReferenceDesignator[] arg1 = new CatlDefaultReferenceDesignatorSet.ReferenceDesignator[arg0];

		int arg2;
		for (arg2 = 0; arg2 < arg0; ++arg2) {
			CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg3 = new CatlDefaultReferenceDesignatorSet.ReferenceDesignator(
					(String) this.expandedReferenceDesignators.get(arg2));
			arg1[arg2] = arg3;
		}

		Arrays.sort(arg1);
		this.expandedReferenceDesignators = new ArrayList(arg0);

		for (arg2 = 0; arg2 < arg0; ++arg2) {
			String arg4 = arg1[arg2].toString();
			this.expandedReferenceDesignators.add(arg2, arg4);
		}

	}
	@Override
	public boolean isDuplicateReferenceDesignators() throws WTException {
		if (this.expandedReferenceDesignators.size() <= 1) {
			return false;
		} else {
			for (int arg0 = 1; arg0 < this.expandedReferenceDesignators.size(); ++arg0) {
				String arg1 = (String) this.expandedReferenceDesignators.get(arg0 - 1);
				String arg2 = (String) this.expandedReferenceDesignators.get(arg0);
				if (arg2.equalsIgnoreCase(arg1)) {
					return true;
				}
			}

			return false;
		}
	}

	private void calculateExpandedReferenceDesignators() throws WTException {
		String arg5 = this.getRangeDelimiter();
		String arg6 = this.getSequenceDelimiter();
		String arg7 = this.getBeginEscapeDelimiter();
		String arg8 = this.getEndEscapeDelimiter();
		String arg9 = arg5 + arg6 + arg7 + arg8;
		byte arg10 = 0;
		String arg11 = null;
		String arg12 = null;
		this.expandedReferenceDesignators = new ArrayList();
		StringTokenizer arg13 = new StringTokenizer(this.consolidatedReferenceDesignators, arg9, true);

		while (true) {
			String arg14;
			do {
				if (!arg13.hasMoreTokens()) {
					if (arg10 == 1) {
						this.addRefDes(arg11);
					}

					String[] arg18;
					if (arg10 == 3) {
						arg18 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
						throw new WTException("wt.part.partResource", "212", arg18);
					}

					if (arg10 == 4) {
						arg18 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
						throw new WTException("wt.part.partResource", "212", arg18);
					}

					return;
				}

				arg14 = arg13.nextToken().trim();
			} while (arg14.length() == 0);

			String[] arg19;
			if (arg14.equals(arg7)) {
				if (arg10 == 2 || arg10 == 1) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				String arg16;
				for (String arg15 = ""; arg13.hasMoreTokens(); arg15 = arg15 + arg16) {
					arg16 = arg13.nextToken().trim();
					if (arg16.equals(arg8)) {
						arg14 = arg15;
						break;
					}

					if (arg16.equals(arg7)) {
						String[] arg17 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
						throw new WTException("wt.part.partResource", "212", arg17);
					}
				}

				if (arg14.equals(arg7)) {
					String[] arg20 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg20);
				}
			}

			switch (arg10) {
			case 0:
				if (arg14.equals(arg5)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				if (arg14.equals(arg6)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				arg10 = 1;
				break;
			case 1:
				if (arg14.equals(arg5)) {
					arg10 = 3;
				} else {
					if (!arg14.equals(arg6)) {
						arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
						throw new WTException("wt.part.partResource", "212", arg19);
					}

					this.addRefDes(arg11);
					arg10 = 4;
				}
				break;
			case 2:
				if (arg14.equals(arg5)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				if (!arg14.equals(arg6)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				arg10 = 4;
				break;
			case 3:
				if (arg14.equals(arg5)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				if (arg14.equals(arg6)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				this.addRange(arg12, arg14, arg5, arg6);
				arg10 = 2;
				break;
			case 4:
				if (arg14.equals(arg5)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				if (arg14.equals(arg6)) {
					arg19 = new String[] { this.consolidatedReferenceDesignators, arg5, arg6, arg7, arg8 };
					throw new WTException("wt.part.partResource", "212", arg19);
				}

				arg10 = 1;
			}

			arg12 = arg11;
			arg11 = arg14;
		}
	}

	private void addRefDes(String arg0) {
		this.expandedReferenceDesignators.add(arg0);
	}

	private void addRange(String arg0, String arg1, String arg2, String arg3) throws WTException {
		CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg4 = new CatlDefaultReferenceDesignatorSet.ReferenceDesignator(
				arg0);
		CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg5 = new CatlDefaultReferenceDesignatorSet.ReferenceDesignator(
				arg1);

		try {
			WTProperties arg6 = WTProperties.getServerProperties();
			long arg7 = (long) arg6.getProperty("wt.part.MaxQuantity", 0);
			String[] arg12;
			if (arg4.getAlpha() == null || arg5.getAlpha() == null || arg4.getNumeric() == null
					|| arg5.getNumeric() == null || !arg4.getAlpha().equalsIgnoreCase(arg5.getAlpha())
					|| arg4.getNumeric().compareTo(arg5.getNumeric()) > 0) {
				arg12 = new String[] { arg0 + arg2 + arg1, arg2, arg3, this.getBeginEscapeDelimiter(),
						this.getEndEscapeDelimiter() };
				throw new WTException("wt.part.partResource", "212", arg12);
			}

			if (!this.isValidNumericPortion(arg4, arg5)) {
				arg12 = new String[] { arg0 + arg2 + arg1 };
				throw new WTException("wt.part.partResource", "263", arg12);
			}

			BigInteger arg9 = arg4.getNumeric();

			while (arg9.compareTo(arg5.getNumeric()) < 1) {
				String arg10 = this.getRefDesString(arg4, arg9);
				this.expandedReferenceDesignators.add(arg10);
				arg9 = arg9.add(BigInteger.ONE);
				if ((long) this.expandedReferenceDesignators.size() > arg7) {
					break;
				}
			}
		} catch (IOException arg11) {
			arg11.printStackTrace();
		}

	}

	private boolean isValidNumericPortion(CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg0,
			CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg1) {
		return !arg0.isZeroPadded() && !arg1.isZeroPadded() ? true
				: (arg0.isZeroPadded() || arg1.isZeroPadded()) && arg0.getNumericLength() == arg1.getNumericLength();
	}

	protected String getRefDesString(CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg0, BigInteger arg1) {
		String arg2 = null;
		if (arg0.isZeroPadded) {
			String arg3 = "%0" + arg0.getNumericLength() + "d";
			String arg4 = String.format(arg3, new Object[] { arg1 });
			arg2 = arg0.getAlpha() + arg4;
		} else {
			arg2 = arg0.getAlpha() + arg1;
		}

		return arg2;
	}

	private void calculateConsolidatedReferenceDesignators(boolean arg0) {
		String rangeDelimiter = this.getRangeDelimiter();
		String sequenceDelimiter = this.getSequenceDelimiter();
		StringBuffer result = new StringBuffer();
		byte arg7 = 0;
		CatlDefaultReferenceDesignatorSet.ReferenceDesignator refDes = null;
		Iterator refDesSize = this.expandedReferenceDesignators.iterator();

		while (true) {
			String refDesStr;
			do {
				do {
					do {
						if (!refDesSize.hasNext()) {
							if (arg7 == 2) {
								result.append(this.getString(refDes, arg0));
							}

							this.consolidatedReferenceDesignators = result.toString();
							return;
						}

						refDesStr = (String) refDesSize.next();
					} while (refDesStr == null);
				} while (refDesStr == "");

				refDesStr = refDesStr.trim();
			} while (refDesStr.length() == 0);

			CatlDefaultReferenceDesignatorSet.ReferenceDesignator tmpRefDes = new CatlDefaultReferenceDesignatorSet.ReferenceDesignator(
					refDesStr);
			switch (arg7) {
			case 0:
				result.append(this.getString(tmpRefDes, arg0));
				arg7 = 1;
				break;
			/**
			 * 屏蔽连续位号合并功能
			 * modify by szeng 2017-07-13
			 */
			case 1:
				arg7 = 1;
				/*	if (tmpRefDes.getAlpha() != null && refDes.getNumeric() != null
						&& tmpRefDes.getAlpha().equalsIgnoreCase(refDes.getAlpha()) && tmpRefDes.getNumeric() != null
						&& refDes.getNumeric() != null
						&& tmpRefDes.getNumeric().equals(refDes.getNumeric().add(BigInteger.ONE))) {
					result.append(rangeDelimiter);
					arg7 = 2;
					break;
				}

				*/
				result.append(sequenceDelimiter);
				result.append(this.getString(tmpRefDes, arg0));
				break;
			case 2:
				if (tmpRefDes.getAlpha() == null || refDes.getAlpha() == null
						//|| !tmpRefDes.getAlpha().equalsIgnoreCase(refDes.getAlpha()) 
						|| tmpRefDes.getNumeric() == null
						|| refDes.getNumeric() == null
						//|| !tmpRefDes.getNumeric().equals(refDes.getNumeric().add(BigInteger.ONE))
						) {
					result.append(this.getString(refDes, arg0));
					result.append(sequenceDelimiter);
					result.append(this.getString(tmpRefDes, arg0));
					arg7 = 1;
				}
			}

			refDes = tmpRefDes;
		}
	}

	private String getString(CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg0, boolean arg1) {
		String arg2 = arg0.toString();
		if (arg1 && (arg2.contains(this.getRangeDelimiter()) || arg2.contains(this.getSequenceDelimiter()))) {
			arg2 = this.getBeginEscapeDelimiter() + arg2 + this.getEndEscapeDelimiter();
		}

		return arg2;
	}

	public boolean equals(Object arg0) {
		if (this == arg0) {
			return true;
		} else if (!(arg0 instanceof ReferenceDesignatorSet)) {
			return false;
		} else {
			try {
				ReferenceDesignatorSet arg1 = (ReferenceDesignatorSet) arg0;
				if (this.getExpandedReferenceDesignators().size() != arg1.getExpandedReferenceDesignators().size()) {
					return false;
				} else {
					for (int arg2 = 0; arg2 < this.getExpandedReferenceDesignators().size(); ++arg2) {
						String arg3 = (String) this.getExpandedReferenceDesignators().get(arg2);
						String arg4 = (String) arg1.getExpandedReferenceDesignators().get(arg2);
						if (!arg3.equalsIgnoreCase(arg4)) {
							return false;
						}
					}

					return true;
				}
			} catch (WTException arg5) {
				return false;
			}
		}
	}

	public int hashCode() {
		return super.hashCode();
	}

	public String getRangeDelimiter() {
		String arg0 = "-";

		try {
			arg0 = (String) PreferenceHelper.service.getValue("/wt/part/referenceDesignator/consolidatedRangeCharacter",
					WTContainerHelper.getExchangeRef().getReferencedContainer(), (WTUser) null);
			if (arg0.length() != 1) {
				throw new WTException(
						"Invalid preference value for consolidated reference designator range character: " + arg0);
			}
		} catch (Exception arg2) {
			System.out.println(
					"Exception while getting consolidated reference designator range character. Using default value of \'-\'.");
			arg2.printStackTrace();
			arg0 = "-";
		}

		return arg0;
	}

	public String getSequenceDelimiter() {
		String arg0 = ",";

		try {
			arg0 = (String) PreferenceHelper.service.getValue(
					"/wt/part/referenceDesignator/consolidatedSeparatorCharacter",
					WTContainerHelper.getExchangeRef().getReferencedContainer(), (WTUser) null);
			if (arg0.length() != 1) {
				throw new WTException(
						"Invalid preference value for consolidated reference designator separator character: " + arg0);
			}
		} catch (Exception arg2) {
			System.out.println(
					"Exception while getting consolidated reference designator separator character. Using default value of \',\'.");
			arg2.printStackTrace();
			arg0 = ",";
		}

		return arg0;
	}

	public String getBeginEscapeDelimiter() {
		String arg0 = "[";

		try {
			arg0 = (String) PreferenceHelper.service.getValue(
					"/wt/part/referenceDesignator/consolidatedStartEscapeCharacter",
					WTContainerHelper.getExchangeRef().getReferencedContainer(), (WTUser) null);
			if (arg0.length() != 1) {
				throw new WTException(
						"Invalid preference value for consolidated reference designator start escape character: "
								+ arg0);
			}
		} catch (Exception arg2) {
			System.out.println(
					"Exception while getting consolidated reference designator start escape character. Using default value of \'[\'.");
			arg2.printStackTrace();
			arg0 = "[";
		}

		return arg0;
	}

	public String getEndEscapeDelimiter() {
		String arg0 = "]";

		try {
			arg0 = (String) PreferenceHelper.service.getValue(
					"/wt/part/referenceDesignator/consolidatedEndEscapeCharacter",
					WTContainerHelper.getExchangeRef().getReferencedContainer(), (WTUser) null);
			if (arg0.length() != 1) {
				throw new WTException(
						"Invalid preference value for consolidated reference designator end escape character: " + arg0);
			}
		} catch (Exception arg2) {
			System.out.println(
					"Exception while getting consolidated reference designator end escape character. Using default value of \']\'.");
			arg2.printStackTrace();
			arg0 = "]";
		}

		return arg0;
	}

	private static class ReferenceDesignator implements Comparable {
		private String refDes;
		private String alpha;
		private BigInteger numeric;
		private int numericLength;
		private boolean isZeroPadded;

		public ReferenceDesignator() {
		}

		public ReferenceDesignator(String arg0) {
			this.initialize(arg0);
		}

		private void initialize(String arg0) {
			this.refDes = arg0;
			if (arg0 == null) {
				this.alpha = null;
				this.numeric = null;
				this.numericLength = 0;
			} else {
				int arg1;
				for (arg1 = arg0.length() - 1; arg1 >= 0 && Character.isDigit(arg0.charAt(arg1)); --arg1) {
					;
				}

				++arg1;
				if (arg1 == 0) {
					this.alpha = null;
					this.numeric = new BigInteger(arg0);
					this.numericLength = arg0.length();
					this.isZeroPadded = arg0.startsWith("0") && this.numericLength > 1;
				} else if (arg1 == arg0.length()) {
					this.alpha = arg0;
					this.numeric = null;
					this.isZeroPadded = false;
					this.numericLength = 0;
				} else {
					this.alpha = arg0.substring(0, arg1);
					this.numeric = new BigInteger(arg0.substring(arg1));
					this.numericLength = arg0.substring(arg1).length();
					this.isZeroPadded = arg0.substring(arg1).startsWith("0") && this.numericLength > 1;
				}

			}
		}

		public String getAlpha() {
			return this.alpha;
		}

		public BigInteger getNumeric() {
			return this.numeric;
		}

		public int getNumericLength() {
			return this.numericLength;
		}

		public boolean isZeroPadded() {
			return this.isZeroPadded;
		}

		public int compareTo(Object arg0) {
			CatlDefaultReferenceDesignatorSet.ReferenceDesignator arg1 = (CatlDefaultReferenceDesignatorSet.ReferenceDesignator) arg0;
			if (this.alpha == null && arg1.alpha != null) {
				return 1;
			} else if (this.alpha != null && arg1.alpha == null) {
				return -1;
			} else {
				if (this.alpha != null && arg1.alpha != null) {
					int arg2 = this.alpha.compareToIgnoreCase(arg1.alpha);
					if (arg2 != 0) {
						return arg2;
					}
				}

				return this.numeric == null && arg1.numeric != null ? -1
						: (this.numeric != null && arg1.numeric == null ? 1
								: (this.numeric == null && arg1.numeric == null ? 0
										: this.numeric.compareTo(arg1.numeric)));
			}
		}

		public boolean equals(Object arg0) {
			return this.compareTo(arg0) == 0;
		}

		public int hashCode() {
			return super.hashCode();
		}

		public String toString() {
			return this.refDes;
		}
	}
}
