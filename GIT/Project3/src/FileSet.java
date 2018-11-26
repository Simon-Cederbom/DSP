import java.io.*;
import java.util.*;

public class FileSet implements Serializable, Set<File>{

	private static final long serialVersionUID = 1L;
	
	ArrayList<File> fileList = new ArrayList<File>();

	@Override
	public int size() {
		return fileList.size();
	}

	@Override
	public boolean isEmpty() {
		return fileList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return fileList.contains(o);
	}

	@Override
	public Iterator<File> iterator() {
		return fileList.iterator();
	}

	@Override
	public Object[] toArray() {
		return fileList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return fileList.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return fileList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return fileList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends File> c) {
		boolean success = true;
		for(Object o : c) {
			if(fileList.contains(o)) {
				success = false;
			}
		}
		if(success) return fileList.addAll((Collection<? extends File>) c);
		else return success;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return fileList.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return fileList.removeAll(c);
	}

	@Override
	public void clear() {
		fileList.clear();
	}

	@Override
	public boolean add(File e) {
		if(fileList.contains(e)) {
			return false;
		}
		return fileList.add(e);
	}

}
