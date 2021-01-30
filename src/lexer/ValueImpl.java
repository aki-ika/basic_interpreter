package lexer;

public class ValueImpl extends Value{
	private String sv;
	private int iv;
	private double dv;
	private boolean bv;
	private ValueType vt;

	public ValueImpl(String s, ValueType vt){
		super(s,vt);
		this.vt = vt;
		switch (vt){
		case INTEGER:
			this.iv = Integer.parseInt(s);
			break;
		case DOUBLE:
			this.dv = Double.parseDouble(s);
			break;
		case BOOL:
			this.bv = Boolean.parseBoolean(s);
			break;
		case STRING:
			this.sv = s;
			break;
		case VOID:
		default:
			break;
		}
	}

	@Override
	public String getSValue() {
		switch(vt) {
		case INTEGER:
			return String.valueOf(iv);
		case DOUBLE:
			return String.valueOf(dv);
		case BOOL:
			return String.valueOf(bv);
		case STRING:
			return sv;
		case VOID:
		default:
			return "エラーです。返せる値がありません";
		}
	}

	@Override
	public int getIValue() {
		return iv;
	}

	@Override
	public double getDValue() {
		return dv;
	}

	@Override
	public boolean getBValue() {
		return bv;
	}

	@Override
	public ValueType getType() {
		return vt;
	}
}
