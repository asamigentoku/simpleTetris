package game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

public class Tetris extends PApplet{
	
	//定義はprivateで設定することにより外部から変更できなくする
	
	// フィールドサイズ
    private final int ROWS = 20, COLS = 10;
 // 1ブロックのピクセルサイズ（30x30）
    private final int BLOCK = 30;
    
    // ゲームボードの状態を表す2次元配列（0: 空, 1: 固定ブロック）
    private int[][] cells = new int[ROWS][COLS];
	
 // タイマー（定期的に actionPerformed を呼び出す）
    private int timer;
    
	
	
	//-------------------
	// 現在操作中のブロック形状（2次元配列）
    private int[][] currentPiece;

	// 現在操作中ブロックの座標（左上基準）
    private int px = 3, py = 0;
    //---------------------
    
    
    
    // ランダムブロック生成用
    private Random rand = new Random();
	
	//Arrays.asListは配列をリストにする方法
    private final List<int[][]> pieces = Arrays.asList(
            new int[][] {
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            },
            new int[][] {
                {2, 2},
                {2, 2}
            },
            new int[][] {
                {0, 3, 0},
                {3, 3, 3},
                {0, 0, 0}
            },
            new int[][] {
                {0, 4, 4},
                {4, 4, 0},
                {0, 0, 0}
            },
            new int[][] {
                {5, 5, 0},
                {0, 5, 5},
                {0, 0, 0}
            },
            new int[][] {
                {6, 0, 0},
                {6, 6, 6},
                {0, 0, 0}
            },
            new int[][] {
                {0, 0, 7},
                {7, 7, 7},
                {0, 0, 0}
            }
     );
    
    // ランダムにブロックを出現させる
    private void spawnPiece() {
        currentPiece = deepCopy(pieces.get(rand.nextInt(pieces.size()))); // パターンからランダム取得
        px = 3;
        py = 0;

        // 初期位置で置けない場合はゲームオーバー
        if (!canMove(0, 0)) {
            //ゲームオーバー処理
        }
    }
    
 // 指定方向に移動できるかどうかチェック
    private boolean canMove(int dx, int dy) {
        for (int y = 0; y < currentPiece.length; y++) {
            for (int x = 0; x < currentPiece[y].length; x++) {
                if (currentPiece[y][x] != 0) {
                    int nx = px + x + dx;
                    int ny = py + y + dy;
                    // 画面外または既存ブロックと衝突していないか
                    if (nx < 0 || nx >= COLS || ny >= ROWS || (ny >= 0 && cells[ny][nx] != 0))
                        return false;
                }
            }
        }
        return true;
    }
    
 // 回転後の形状で配置可能かを判定
    private boolean canRotate(int[][] rotated) {
        for (int y = 0; y < rotated.length; y++) {
            for (int x = 0; x < rotated[y].length; x++) {
                if (rotated[y][x] != 0) {
                    int nx = px + x;
                    int ny = py + y;
                    if (nx < 0 || nx >= COLS || ny >= ROWS || (ny >= 0 && cells[ny][nx] != 0))
                        return false;
                }
            }
        }
        return true;
    }
    
 // ブロックの回転処理（90度右回転）
    private void rotatePiece() {
        int size = currentPiece.length;
        int[][] rotated = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                rotated[x][size - 1 - y] = currentPiece[y][x];
            }
        }
        if (canRotate(rotated)) {
            currentPiece = rotated;
        }
    }
    
    
    
    // 2次元配列のディープコピー
    private int[][] deepCopy(int[][] shape) {
        int[][] copy = new int[shape.length][];
        for (int i = 0; i < shape.length; i++) {
            copy[i] = Arrays.copyOf(shape[i], shape[i].length);
        }
        return copy;
    }
    
 // ブロックを盤面に固定（マージ）
    private void mergePiece() {
        for (int y = 0; y < currentPiece.length; y++) {
            for (int x = 0; x < currentPiece[y].length; x++) {
                if (currentPiece[y][x] != 0) {
                    cells[py + y][px + x] =currentPiece[y][x];
                }
            }
        }
        clearLines();  // ライン消去
        spawnPiece();  // 次のブロックを出現
    }
    
 // ラインが揃っていたら消去
    private void clearLines() {
        for (int y = ROWS - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < COLS; x++) {
                if (cells[y][x] == 0) full = false;
            }
            if (full) {
                // 上の行を下にずらす
                for (int row = y; row > 0; row--) {
                    cells[row] = cells[row - 1].clone();
                }
                cells[0] = new int[COLS]; // 一番上は空に
                y++; // 同じ行をもう一度確認（複数ライン消し対応）
            }
        }
    }
    
    
	
	//settingは一回実行
	@Override 
	public void settings() {
		//画面の大きさを設定
		size(300,600); 
	}
	
	//setup()は一回実行
	@Override
	public void setup(){
		//ここに初期設定を書く
		background(50);
        stroke(192);
        //縦棒を描写
        for (int i = 1; i <=COLS; i++) {
            line(BLOCK* i,0,BLOCK* i,600);
            
        }
        //横棒を描写
        for(int i=1;i<ROWS;i++){
        	line(0,BLOCK* i,300,BLOCK* i);
        }
        noStroke();
        fill(0);
        
        
        //現在の状態を描写
        cells = new int[20][10];  // 10列×20行のグリッド
        
        spawnPiece();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 20; y++) {
            	int v = cells[y][x];
            	if (v != 0) {
            		switch (v) {
            	    case 1: 
            	        fill(0, 255, 255); break;    // I: シアン
            	    case 2: 
            	        fill(255, 255, 0); break;    // O: 黄色
            	    case 3: 
            	        fill(128, 0, 128); break;    // T: 紫
            	    case 4: 
            	        fill(0, 255, 0); break;      // S: 緑
            	    case 5: 
            	        fill(255, 0, 0); break;      // Z: 赤
            	    case 6: 
            	        fill(0, 0, 255); break;      // J: 青
            	    case 7: 
            	        fill(255, 165, 0); break;    // L: オレンジ
            	    default: 
            	        fill(100); break;            // その他はグレー
            	    }
                    //長方形を描写する
                    rect(x * 30 + 1, y * 30 + 1, 28, 28);
                }
            }
        }


        
	}
	
	
	//何回も実行
	@Override
	public void draw(){
		
		
		//落下と回転
		//衝突判定
		//描写
		if(timer%100==9) {
			actionPerformed();
		}
			
		//ミノを描写
		MinoDraw();
		timer+=1;
	}
	
	void MinoDrop(){
	}
	
	
	
	
	//落下可能かどうか
	boolean candraw(int dx, int dy){
		for (int y = 0; y <20; y++) {
            for (int x = 0; x <10; x++) {
                if (cells[y][x] != 0) {
                    int nx = x + dx;
                    int ny = y + dy;
                    // 画面外または既存ブロックと衝突していないか
                    if (nx < 0 || nx >= 10 || ny >= 20 || (ny >= 0 &&cells[ny][nx] !=0))
                        return false;
                }
            }
        }
        return true;
	}
	
	
	
	
	void MinoDraw(){
		background(50); 
		stroke(192);
        //縦棒を描写
        for (int i = 1; i <=COLS; i++) {
            line(BLOCK* i,0,BLOCK* i,600);
            
        }
        //横棒を描写
        for(int i=1;i<ROWS;i++){
        	line(0,BLOCK* i,300,BLOCK* i);
        }
        noStroke();
        fill(0);
		// 固定されたブロックを描画
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
            	int v = cells[y][x];
            	if (v != 0) {
            		switch (v) {
            	    case 1: 
            	        fill(0, 255, 255); break;    // I: シアン
            	    case 2: 
            	        fill(255, 255, 0); break;    // O: 黄色
            	    case 3: 
            	        fill(128, 0, 128); break;    // T: 紫
            	    case 4: 
            	        fill(0, 255, 0); break;      // S: 緑
            	    case 5: 
            	        fill(255, 0, 0); break;      // Z: 赤
            	    case 6: 
            	        fill(0, 0, 255); break;      // J: 青
            	    case 7: 
            	        fill(255, 165, 0); break;    // L: オレンジ
            	    default: 
            	        fill(100); break;            // その他はグレー
            	    }
                    //長方形を描写する
                    rect(x * BLOCK + 1, y * BLOCK + 1,BLOCK-2,BLOCK-2);
                }
            }
        }
        
        //現在操作中のミノを表示
        for (int y = 0; y < currentPiece.length; y++) {
            for (int x = 0; x < currentPiece[y].length; x++) {
            	int v = currentPiece[y][x];
            	if (v != 0) {
            		switch (v) {
            	    case 1: 
            	        fill(0, 255, 255); break;    // I: シアン
            	    case 2: 
            	        fill(255, 255, 0); break;    // O: 黄色
            	    case 3: 
            	        fill(128, 0, 128); break;    // T: 紫
            	    case 4: 
            	        fill(0, 255, 0); break;      // S: 緑
            	    case 5: 
            	        fill(255, 0, 0); break;      // Z: 赤
            	    case 6: 
            	        fill(0, 0, 255); break;      // J: 青
            	    case 7: 
            	        fill(255, 165, 0); break;    // L: オレンジ
            	    default: 
            	        fill(100); break;            // その他はグレー
            	    }
                    //長方形を描写する
            		rect((px + x) * BLOCK + 1, (py + y) * BLOCK + 1, BLOCK - 2, BLOCK - 2);
                }
            }
        }
		
		
	}
	
	@Override
	public void keyPressed() {
	    switch (keyCode) {
	        case LEFT:
	            if (canMove(-1, 0)) px--;
	            break;
	        case RIGHT:
	            if (canMove(1, 0)) px++;
	            break;
	        case DOWN:
	            if (canMove(0, 1)) py++;
	            break;
	        case UP:
	            rotatePiece();
	            break;
	    }
	}
    
 // タイマーから呼ばれる落下処理
    public void actionPerformed() {
        if (canMove(0, 1)) {
            py++; // 1マス下に移動
        } else {
            mergePiece(); // 着地 → 固定化
        }
        MinoDraw(); // 画面更新
    }
	
	
	
	
	
	
	
	public static void main(String[] args){
		PApplet.main(Tetris.class.getName());
	}
}

