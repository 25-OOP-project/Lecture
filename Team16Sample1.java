// package team16;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Team16Sample1 {

	public static void main(String[] args) {
		ManagerContainer managers = new ManagerContainer();
		MainWindow win = new MainWindow(managers); // MainWindow 객체 생성
		win.setSize(600, 600); // 화면 사이즈
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료

	}

}

class MainWindow extends JFrame {

	//각 기능 Manager 초기화
	ManagerContainer managers;
	LectureManager lecmanager = null;
	AttendanceManager attmanager = null;
	MemoManager memomanager = null;
	ScheduleManager schmanager = null;


	JPanel menupanel = null;
	JPanel centerpanel = null;
	JPanel lecturepanel = null;
	JButton addbtn = null;
	JButton viewbtn = null;
	JButton editbtn = null;
	JButton deletebtn = null;
	// JButton sortbtn = null;
	JButton attbtn = null;
	JButton membtn = null;
	JButton schbtn = null;
	JTextArea jta = null;
	JTextField tflecture = null;
	JTextField tfprofessor = null;
	JTextField tftime = null;
	JTextField tfview = null;

	DefaultListModel<String> listModel = new DefaultListModel<>();
	JList<String> lectureList = null;

	// MainWindow(MainUI)
	MainWindow(ManagerContainer managers) {
		setLayout(new BorderLayout());

		this.managers=managers;
		// 각 기능 매니저 받아오기
		lecmanager = managers.getLectureManager();
		attmanager = managers.getAttendanceManager();
		memomanager = managers.getMemoManager();
		schmanager = managers.getScheduleManager();


		// 프로그램 시작 시 저장된 데이터 불러오기(FileIO)
        List<Lecture> loaded = FileIO.loadLectures();
        LectureManager.fromList(lecmanager, loaded);
        for (int i = 0; i < lecmanager.lecturecnt; i++) {
            Lecture lec = lecmanager.lecture[i];
            listModel.addElement(String.format("%-20s %-20s %-10s",
                lec.getLecturename(), lec.getProfessorname(), lec.getTime()));
        }
        // List<AttendanceRecord> attendanceList = FileIO.loadAttendance();
        // attmanager.setRecords(attendanceList);
        // Map<String, List<String>> schedules = FileIO.loadSchedule();
        // schmanager.setScheduleMap(schedules);



		lecturepanel = new JPanel();
		lecturepanel.setLayout(new GridLayout(4, 4));
		lecturepanel.add(new JLabel("Lecture"));
		lecturepanel.add(new JLabel("Professor"));
		lecturepanel.add(new JLabel("Time"));
		lecturepanel.add(new JLabel(" "));

		tflecture = new JTextField();
		lecturepanel.add(tflecture);
		tfprofessor = new JTextField();
		lecturepanel.add(tfprofessor);
		tftime = new JTextField();
		lecturepanel.add(tftime);
		// 추가 버튼
		addbtn = new JButton("추가");
		lecturepanel.add(addbtn);

		// Search Label
		lecturepanel.add(new JLabel("Search"));
		lecturepanel.add(new JLabel(" "));
		lecturepanel.add(new JLabel(" "));
		lecturepanel.add(new JLabel(" "));

		// 조회 텍스트 필드
		tfview = new JTextField();
		lecturepanel.add(tfview);
		// 조회 버튼
		viewbtn = new JButton("조회");
		lecturepanel.add(viewbtn);
		// 수정 버튼
		editbtn = new JButton("수정");
		lecturepanel.add(editbtn);
		// 삭제 버튼
		deletebtn = new JButton("삭제");
		lecturepanel.add(deletebtn);
		add(lecturepanel, BorderLayout.NORTH);
		/*
		 * 정렬 버튼 sortbtn = new JButton("정렬"); lecturepanel.add(sortbtn);
		 * add(lecturepanel, BorderLayout.NORTH);
		 */

		// centerpanel(중앙 - 강의 목록)
		centerpanel = new JPanel();
		centerpanel.setLayout(new FlowLayout());
		lectureList = new JList<>(listModel);
		lectureList.setVisibleRowCount(40);
		lectureList.setFixedCellWidth(400);
		centerpanel.add(lectureList);
		add(centerpanel, BorderLayout.CENTER);

		// menupanel(아래 - 출석, 메모, 일정 버튼)
		menupanel = new JPanel();
		menupanel.setLayout(new GridLayout(1, 3));
		attbtn = new JButton("출석");
		menupanel.add(attbtn);
		membtn = new JButton("메모");
		menupanel.add(membtn);
		schbtn = new JButton("일정");
		menupanel.add(schbtn);
		add(menupanel, BorderLayout.SOUTH);

		addbtn.addActionListener(new AddListener(this)); // addbtn(버튼) 객체에 Event Listener 등록. 버튼에 클릭 이벤트를 등록하고, 클릭 시
															// AddListener 클래스에서 정의한 로직이 실행되도록 함.
		deletebtn.addActionListener(new DeleteListener(this)); // deletebtn(버튼) 객체에 Event Listener 등록. 버튼에 클릭 이벤트를 등록하고,
																// 클릭 시 AddListener 클래스에서 정의한 로직이 실행되도록 함.
		editbtn.addActionListener(new EditListener(this)); // editbtn(버튼) 객체에 Event Listener 등록. 버튼에 클릭 이벤트를 등록하고, 클릭 시
															// AddListener 클래스에서 정의한 로직이 실행되도록 함.
		viewbtn.addActionListener(new ViewListener(this)); // viewbtn(버튼) 객체에 Event Listener 등록. 버튼에 클릭 이벤트를 등록하고, 클릭 시
															// AddListener 클래스에서 정의한 로직이 실행되도록 함.


		 //창 닫을 때 데이터 저장
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<Lecture> current = LectureManager.toList(lecmanager);
                FileIO.saveLectures(current);
                // FileIO.saveAttendance(attmanager.getRecords());
                // FileIO.saveSchedule(schmanager.getScheduleMap());
                System.exit(0);
            }
        });
	}

}

// Lecture 클래스
class Lecture implements Serializable {
	String lecturename = null;
	String professorname = null;
	String time = null;
	boolean attend = false;

	Lecture() {
	} // 빈 생성자

	Lecture(String l, String p, String t) { // 인수를 받는 생성자
		lecturename = l;
		professorname = p;
		time = t;
	}

	void editLecturename(String s) { // 강의명 수정 함수
		lecturename = s;

	}

	void editProfessorname(String s) { // 교수명 수정 함수
		professorname = s;
	}

	void editTime(String s) { // 강의시간 수정 함수
		time = s;
	}

	void attendCheck() { // 출석 체크 함수
		attend = true;
	}

	void abscentCheck() { // 결석 체크 함수
		attend = false;
	}

	// FileIO용 get()/set()
	public String getLecturename() {
		return lecturename;
	}

	public void setLecturename(String lecturename) {
		this.lecturename = lecturename;
	}

	public String getProfessorname() {
		return professorname;
	}

	public void setProfessorname(String professorname) {
		this.professorname = professorname;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}

// 배열 기반 LectureManager
class LectureManager {
	Lecture[] lecture = new Lecture[10]; // 강의를 리스트로 추가
	int lecturecnt = 0; // 강의 개수

    //fileio와 연결
	// 배열 → 리스트
    public static List<Lecture> toList(LectureManager mgr) {
        List<Lecture> temp = new ArrayList<>();
        for (int i = 0; i < mgr.lecturecnt; i++) {
            temp.add(mgr.lecture[i]);
        }
        return temp;
    }
    // 리스트 → 배열
    public static void fromList(LectureManager mgr, List<Lecture> list) {
        int size = Math.min(list.size(), mgr.lecture.length);
        for (int i = 0; i < size; i++) {
            mgr.lecture[i] = list.get(i);
        }
        mgr.lecturecnt = size;
    }



	void addLecture(Lecture newLecture) { // 강의명 추가 함수
		if (lecturecnt < lecture.length) {
			lecture[lecturecnt++] = newLecture;
		}
	}

	void deleteLecture(int index) { // 강의명 삭제 함수
		if (index >= 0 && index < lecturecnt) {
			// 삭제된 강의를 배열의 뒤로 이동
			for (int i = index; i < lecturecnt - 1; i++) {
				lecture[i] = lecture[i + 1];
			}
			lecture[--lecturecnt] = null; // 마지막 요소 제거
		}
	}

	/*
	 * void sortLecture() { // 강의명 정렬 함수(아직 미실행) int i, j; Lecture temp;
	 * 
	 * for (i=0; i<lecturecnt-1; i++) { for (j=i+1; j<lecturecnt; j++) {
	 * if(lecture[i].time > lecture[j].time) { temp = lecture[i]; lecture[i] =
	 * lecture[j]; lecture[j] = temp; } } } }
	 */

}

// Event Listener 클래스
class AddListener implements ActionListener {

	MainWindow win = null;

	public void actionPerformed(ActionEvent e) {
		if (win.lecmanager.lecturecnt < 10) {
			String name = win.tflecture.getText();
			String prof = win.tfprofessor.getText();
			String time = win.tftime.getText();

			Lecture lec = new Lecture(name, prof, time);

			win.lecmanager.addLecture(lec);
			win.listModel.addElement(String.format("%-20s %-20s %-10s", name, prof, time));
			win.tflecture.setText("");
			win.tfprofessor.setText("");
			win.tftime.setText("");

			System.out.println("강의 추가됨: " + name);  //콘솔로 확인인
		}
	}
	AddListener(MainWindow w) {
		win = w;
	}
}


class ViewListener implements ActionListener {

	MainWindow win = null;

	public void actionPerformed(ActionEvent e) {
		String keyword = win.tfview.getText().trim().toLowerCase();
		win.listModel.clear();

		if (keyword.isEmpty()) {
			// 검색어가 없으면 모든 강의 보여주기
			for (int i = 0; i < win.lecmanager.lecturecnt; i++) {
				Lecture lec = win.lecmanager.lecture[i];
				if (lec != null) {
					win.listModel.addElement(
							String.format("%-20s %-20s %-10s", lec.lecturename, lec.professorname, lec.time));
				}
			}
			return;
		}

		// keyword(강의명, 교수명, 시간) 포함하는 강의만 보여주기
		for (int i = 0; i < win.lecmanager.lecturecnt; i++) {
			Lecture lec = win.lecmanager.lecture[i];
			if (lec != null) {
				if (lec.lecturename.toLowerCase().contains(keyword) || lec.professorname.toLowerCase().contains(keyword)
						|| lec.time.toLowerCase().contains(keyword)) {
					win.listModel.addElement(
							String.format("%-20s %-20s %-10s", lec.lecturename, lec.professorname, lec.time));
				}
			}
		}
	}

	ViewListener(MainWindow w) {
		win = w;
	}
}

class EditListener implements ActionListener {

	MainWindow win = null;

	public void actionPerformed(ActionEvent e) { // 수정 리스너
		int selectedIndex = win.lectureList.getSelectedIndex(); // 선택된 항목 인덱스 확인
		if (selectedIndex != -1) {
			// 강의 수정
			String newLectureName = win.tflecture.getText();
			String newProfessorName = win.tfprofessor.getText();
			String newTime = win.tftime.getText();
			// LectureManager 배열 업데이트
			win.lecmanager.lecture[selectedIndex].editLecturename(newLectureName);
			win.lecmanager.lecture[selectedIndex].editProfessorname(newProfessorName);
			win.lecmanager.lecture[selectedIndex].editTime(newTime);
			// JList 모델 업데이트
			win.listModel.set(selectedIndex, String.format("%-20s %-20s %-10s", newLectureName, newProfessorName, newTime));
			// 입력 필드 초기화
			win.tflecture.setText("");
			win.tfprofessor.setText("");
			win.tftime.setText("");

		}
	}

	EditListener(MainWindow w) {
		win = w;
	}
}

class DeleteListener implements ActionListener {
			
			MainWindow win = null;
			
			public void actionPerformed(ActionEvent e) {	//삭제 리스너
				int selectedIndex = win.lectureList.getSelectedIndex();
				if (selectedIndex != -1) {
		          // 모델에서 제거
		          win.listModel.remove(selectedIndex);
		          // LectureManager 배열에서도 삭제 (간단하게 구현)
		          for (int i = selectedIndex; i < win.lecmanager.lecturecnt - 1; i++) {
		              win.lecmanager.lecture[i] = win.lecmanager.lecture[i + 1];
		          }
		          win.lecmanager.lecture[win.lecmanager.lecturecnt - 1] = null;
		          win.lecmanager.lecturecnt--;
		      }
			}
			
			DeleteListener (MainWindow w)
			{
				win = w;
			}
}
		


// 6월 6일 첫째주 교수님 코멘트 받기(팀별), 16일 오전 9시 기말 발표




