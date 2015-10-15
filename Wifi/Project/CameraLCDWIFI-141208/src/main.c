/**
  ******************************************************************************
  * @file    OV9655_Camera/src/main.c  
  * @author  MCD Application Team
  * @version V1.0.0
  * @date    18-April-2011
  * @brief   Main program body.
  ******************************************************************************
  * @attention
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
  * DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING
  * FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE
  * CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  *
  * <h2><center>&copy; Portions COPYRIGHT 2011 STMicroelectronics</center></h2>
  ******************************************************************************
  */ 
/**
  ******************************************************************************
  * <h2><center>&copy; Portions COPYRIGHT 2012 Embest Tech. Co., Ltd.</center></h2>
  * @file    main.c 
  * @author  CMP Team
  * @version V1.0.0
  * @date    28-December-2012
  * @brief   Main program body.                        
  *          Modified to support the STM32F4DISCOVERY, STM32F4DIS-BB, STM32F4DIS-CAM
  *          and STM32F4DIS-LCD modules. 
  ******************************************************************************
  * @attention
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, Embest SHALL NOT BE HELD LIABLE FOR ANY DIRECT, INDIRECT
  * OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING FROM THE CONTENT
  * OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE CODING INFORMATION
  * CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  ******************************************************************************
  */
/* Includes ------------------------------------------------------------------*/
#include "stm32f4xx.h"
#include "stm32f4_discovery.h"
#include "stm32f4_discovery_lcd.h"
#include "stm32f4_discovery_lis302dl.h"
#include "main.h"
#include "bmp.h"
#include "dcmi_ov9655.h"
#include "ff.h"

/** @addtogroup STM32F4xx_StdPeriph_Examples
  * @{
  */

/** @addtogroup DCMI_OV9655_Camera
  * @{
  */ 

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
#define DCMI_DR_ADDRESS     0x50050028
#define FSMC_LCD_ADDRESS    0x60100000

#define BTN_0		0x0400
#define BTN_1		0x0001
#define BTN_2		0x0002
#define BTN_3		0x0004
#define BTN_4		0x0008
#define BTN_5		0x0010
#define BTN_6		0x0020
#define BTN_7		0x0040
#define BTN_8		0x0080
#define BTN_9		0x0100
#define BTN_X		0x0200
#define BTN_S		0x0800

/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/


#define ID_INPUT_SIZE 15
#define MESSAGE_SIZE 100
#define RX_BUFFER_SIZE 4096

int16_t  pos_y = 0;
int16_t  pos_x = 60;

unsigned short prev_KeyPad_V = 0, curr_KeyPad_V = 0;
char displayModeBuffer = 0;
char password[4] = {'0','0','0','0'};
char keyReturn = 0;
char tmp[100];

char displayMode='1'; // display change
char recieveData; // usart input data
char message[MESSAGE_SIZE];  // the message for user from app
int uartRxCounter=0;  //variable for message
char userData[ID_INPUT_SIZE]; // IDnumber
char purpose[1];
int inforNumber=0; //purpose of visit
int usartRxInterruptStatus=0; // 
int endMsm=0;
char status[1];
char key_input_userdata(void);
char key_input_menu(void);



uint8_t KeyPressFlg = 0;
__IO uint32_t TimingDelay;
RCC_ClocksTypeDef RCC_Clocks;
EXTI_InitTypeDef   EXTI_InitStructure;
uint8_t capture_Flag = ENABLE;

extern FATFS filesys;        // volume lable 

extern uint8_t image_buf[1024];
extern uint32_t pic_counter;

extern uint32_t RxBufferTail;
extern uint32_t RxBufferHead;
extern char domain[100];

/* Private function prototypes -----------------------------------------------*/
uint8_t DCMI_OV9655Config(void);
void DCMI_Config(void);
void I2C1_Config(void);
void EXTILine0_Config(void);
void LIS302DL_Reset(void);

void DBGU_Init(void);
int wifi_main(void);

void Key_Pad_Config(void);
unsigned short Key_Pad_Check(void);
int32_t Background_DISPLAY(void);

unsigned int timerkey;
extern int test_domain(char *domain, char *purpose, char *number, char isHttps);
extern int test_change(char *domain, char *status, char isHttps);
extern int test_rx(char *domain, char isHttps);
extern char RxBuffer[RX_BUFFER_SIZE];

int check_status(void)
{
	int i=0;
	test_rx(domain, 0);
	
	Delay(100);
	for(i=0; i<4096; i++)
	{
		if(RxBuffer[i] == 'Z')
		{
			RxBuffer[i] = 'x';
			if(RxBuffer[i+1]>'0' && RxBuffer[i+1] <'6')
			{
				if(displayMode != RxBuffer[i+1])
				{
					displayMode = RxBuffer[i+1];
					return 1;
				}
			}
		}
	}
	return 0;
}
	/*
	for(i=0; i<4096; i++)
	{
		RxBuffer[i] = '\0';
	}*/
			
/* Private functions ---------------------------------------------------------*/
/**
  * @brief  Main program.
  * @param  None
  * @retval None
  */

/*void LCD_KEYPAD_DISPLAY(void)
{
		LCD_Clear(LCD_COLOR_WHITE);
		LCD_DisplayStringLine(LINE(1), "  1  2  3");
		LCD_DisplayStringLine(LINE(3), "  4  5  6");
		LCD_DisplayStringLine(LINE(5), "  7  8  9");
		LCD_DisplayStringLine(LINE(7), "  *  0  #");
		LCD_DisplayStringLine(LINE(9), "  _______");
		LCD_DrawRect(160, 60, 160, 120);

}*/

int32_t LCD_PREVIEW_DISPLAY(void)
{
		int32_t  ret = -1;
		int32_t  i = 0;
		int32_t  j = 0;
		uint16_t *ptr16t;
		int16_t  pos_y = 0;
		uint32_t bw = 0;
		char  file_str[30] = {0};
		FIL file;        // File object 


	  // mount the filesys 
	  if (f_mount(0, &filesys) != FR_OK) {
		return -1;
	  }
	  Delay(10);

	  sprintf(file_str, "pic%d.bmp",pic_counter-1);

	  ret = f_open(&file, file_str, FA_READ);
	  if (ret) {
		return ret;
	  }

	ret = f_lseek(&file,70);
    if (ret) {
		return ret;
	}
/*
	  // read the bmp header 
	  ret = f_read(&file, image_buf, 70, &bw);
	printf("\r\n");
	for(i=0;i<70;i++)
	{
		printf("x%02X ",image_buf[i]);
	}
	printf("\r\n");
*/
	  
	 pos_y = 180;
	  
	 for (j = 0; j < 240; j+=2) 
	{
		LCD_SetCursor(160,pos_y--);
		LCD_WriteRAM_Prepare();
		ret = f_read(&file, image_buf, 640, &bw);
		ptr16t = (uint16_t *) image_buf;
		for(i=0;i<320;i+=2) 
		{ 
			LCD_WriteRAM (ptr16t[i]);
		}
//		ret = f_read(&file, image_buf, 640, &bw);
	}

	  ret = f_close(&file);

	  f_mount(0, NULL);

	  return ret;
	
	
}


int32_t img_DISPLAY(char *ptr)
{
		int32_t  ret = -1;
		int32_t  i = 0;
		int32_t  j = 0;
		uint16_t *ptr16t;
		int16_t  pos_y = 0;
		uint32_t bw = 0;
		char  file_str[30] = {0};
		FIL file;        // File object 
		char filename[10]={0,};
		char cnt=0;

		while(1)
		{
				if(ptr)
				{
						if((' ' <= *(ptr)) && (*(ptr) <= '~'))
						{
							filename[cnt] = *ptr++;
							cnt++;
						}
						else
						{
							cnt=0;
							break;
						}
				}
		}
	  // mount the filesys 
	  if (f_mount(0, &filesys) != FR_OK) {
		return -1;
	  }
	  Delay(10);

	  sprintf(file_str,filename);

	  ret = f_open(&file, file_str, FA_READ);
	  if (ret) {
		  printf("%s file open fail!!!!\r\n", filename);
		return ret;
	  }
	  printf("%s file open success!!!!\r\n", filename);

	ret = f_lseek(&file,72);
    if (ret) {
		return ret;
	}
/*
	  // read the bmp header 
	  ret = f_read(&file, image_buf, 70, &bw);
	printf("\r\n");
	for(i=0;i<70;i++)
	{
		printf("x%02X ",image_buf[i]);
	}
	printf("\r\n");
*/
	  
	 pos_y = 239;
	  
	 for (j = 0; j < 240; j++) 
	{
		LCD_SetCursor(0,pos_y--);
		LCD_WriteRAM_Prepare();
		ret = f_read(&file, image_buf, 640, &bw);
		ptr16t = (uint16_t *) image_buf;
		for(i=0;i<320;i++) 
		{ 
			LCD_WriteRAM (ptr16t[i]);
		}
//		ret = f_read(&file, image_buf, 640, &bw);
	}

	  ret = f_close(&file);

	  f_mount(0, NULL);

	  return ret;
	
	
}

int32_t num_DISPLAY(char *ptr)
{
		int32_t  ret = -1;
		int32_t  i = 0;
		int32_t  j = 0;
		uint16_t *ptr16t;
		uint32_t bw = 0;
		char  file_str[30] = {0};
		FIL file;        // File object 
		char filename[10]={0,};
		char cnt=0;

		while(1)
		{
				if(ptr)
				{
						if((' ' <= *(ptr)) && (*(ptr) <= '~'))
						{
							filename[cnt] = *ptr++;
							cnt++;
						}
						else
						{
							cnt=0;
							break;
						}
				}
		}
	  // mount the filesys 
	  if (f_mount(0, &filesys) != FR_OK) {
		return -1;
	  }
	  Delay(10);

	  sprintf(file_str,filename);

	  ret = f_open(&file, file_str, FA_READ);
	  if (ret) {
		  printf("%s file open fail!!!!\r\n", filename);
		return ret;
	  }
	  printf("%s file open success!!!!\r\n", filename);

	ret = f_lseek(&file,70);
    if (ret) {
		return ret;
	}
/*
	  // read the bmp header 
	  ret = f_read(&file, image_buf, 70, &bw);
	printf("\r\n");
	for(i=0;i<70;i++)
	{
		printf("x%02X ",image_buf[i]);
	}
	printf("\r\n");
*/
	  
	 pos_y = 176;
	
	 for (j = 0; j < 20; j++) 
	{
		LCD_SetCursor(pos_x,pos_y--);
		LCD_WriteRAM_Prepare();
		ret = f_read(&file, image_buf, 36, &bw);
		ptr16t = (uint16_t *) image_buf;
		for(i=0;i<18;i++) 
		{
			LCD_WriteRAM (ptr16t[i]);
		}
//		ret = f_read(&file, image_buf, 640, &bw);
	}

	  ret = f_close(&file);

	  f_mount(0, NULL);

	  return ret;
	
	
}

int32_t num_CLEAR(void)
{
		int32_t  ret = -1;
		int32_t  i = 0;
		int32_t  j = 0;
		uint16_t *ptr16t;
		uint32_t bw = 0;
		char  file_str[30] = {0};
		FIL file;        // File object

	  // mount the filesys 
	  if (f_mount(0, &filesys) != FR_OK) {
		return -1;
	  }
	  Delay(10);

	  sprintf(file_str,"clear.bmp");

	  ret = f_open(&file, file_str, FA_READ);
	  if (ret) {
		  printf("%s file open fail!!!!\r\n", "clear.bmp");
		return ret;
	  }
	  printf("%s file open success!!!!\r\n", "clear.bmp");

	ret = f_lseek(&file,70);
    if (ret) {
		return ret;
	}
/*
	  // read the bmp header 
	  ret = f_read(&file, image_buf, 70, &bw);
	printf("\r\n");
	for(i=0;i<70;i++)
	{
		printf("x%02X ",image_buf[i]);
	}
	printf("\r\n");
*/
	  
	 pos_y = 176;
	
	 for (j = 0; j < 20; j++) 
	{
		LCD_SetCursor(60,pos_y--);
		LCD_WriteRAM_Prepare();
		ret = f_read(&file, image_buf, 396, &bw);
		ptr16t = (uint16_t *) image_buf;
		for(i=0;i<198;i++) 
		{
			LCD_WriteRAM (ptr16t[i]);
		}
//		ret = f_read(&file, image_buf, 640, &bw);
	}

	  ret = f_close(&file);

	  f_mount(0, NULL);

	  return ret;
}

// send picture to server by WIFI   by KMC
int32_t sendToServer() // information 
{
		int32_t  ret = -1;
//		int32_t  i = 0;
		int32_t  j = 0;
		uint16_t *ptr16t;
//		int16_t  pos_y = 0;
		uint32_t bw = 0;
		char  file_str[30] = {0};
		FIL file;        // File object 

	
	  // mount the filesys 
	  if (f_mount(0, &filesys) != FR_OK) {
		return -1;
	  }
	  Delay(10);

	  sprintf(file_str, "pic%d.bmp",pic_counter-1);

	  ret = f_open(&file, file_str, FA_READ);
	  if (ret) {
		return ret;
	  }

	ret = f_lseek(&file,70);
    if (ret) {
		return ret;
	}
	
	SN8200_HAL_SendData("#No,",4); // alram for sending information
	SN8200_HAL_SendData(userData,ID_INPUT_SIZE); //user ID
	switch(inforNumber){
		case 1:
			SN8200_HAL_SendData(",수업질문,",10);
			break;
		case 2:
			SN8200_HAL_SendData(",성적,",6);
			break;
		case 3:
			SN8200_HAL_SendData(",상담,",6);
			break;
		case 4:
			SN8200_HAL_SendData(",기타,",6);
			break;
		case 5:
			SN8200_HAL_SendData(",사전약속,",10);
			break;
		case 6:
			SN8200_HAL_SendData(",문의,",6);
			break;
		case 7:
			SN8200_HAL_SendData(",제안,",6);
			break;
		case 8:
			SN8200_HAL_SendData(",기타,",6);
			break;
	}
	
	//서버로 보낼때는 bmpheader 보내야하는건지!
/*
	  // read the bmp header 
	  ret = f_read(&file, image_buf, 70, &bw);
	printf("\r\n");
	for(i=0;i<70;i++)
	{
		printf("x%02X ",image_buf[i]);
	}
	printf("\r\n");
*/
	  
	// pos_y = 180;
	
	 for (j = 0; j < 240; j++) 
	{
//LCD에 그리는 함수 두개 주석처리.
		//LCD_SetCursor(160,pos_y--);
		//LCD_WriteRAM_Prepare();
		ret = f_read(&file, image_buf, 640, &bw);
		ptr16t = (uint16_t *) image_buf;

//1줄씩 서버로 전송 
		SN8200_HAL_SendData(ptr16t,320); // unsigned char , size 
		
		/*for(i=0;i<320;i++) 
		{ 
			USART_SendData(USART3,ptr16t[i]); //send data by USART3
		}*/
//		ret = f_read(&file, image_buf, 640, &bw);
	}

	  ret = f_close(&file);

	  f_mount(0, NULL);

	  return ret;
	
	
}










int main(void)
{
  /* SysTick end of count event each 10ms */
  RCC_GetClocksFreq(&RCC_Clocks);
  
 
  SysTick_Config(RCC_Clocks.HCLK_Frequency / 1000);

  DBGU_Init();
  Key_Pad_Config();
	
//  LIS302DL_Reset();
	
  printf("\r\n");
	printf("================================================\r\n");
  printf("       SmartBell project , Kookmin Univ.\r\n");
	printf("================================================\r\n");

  // Initialize the LCD 
  STM32f4_Discovery_LCD_Init();
  LCD_Clear(LCD_COLOR_WHITE);
  LCD_SetTextColor(LCD_COLOR_BLUE);

  DCMI_Control_IO_Init();

  LCD_DisplayStringLine(LINE(4), "    Camera Init..");

	if (f_mount(0, &filesys) != FR_OK)
	{
		printf("\r\nSD Mount Fail !\r\n\r\n");
	} else {
		init_picture_count();
		printf("\r\nSD Picture Count : %d\r\n",pic_counter);
	}
	
	
	if (DCMI_OV9655Config() == 0x00)
	{
		LCD_DisplayStringLine(LINE(6), "    Camera Init OK!");

		img_DISPLAY("Title.bmp");

		//LCD_KEYPAD_DISPLAY();
		LCD_Clear(LCD_COLOR_WHITE);
		
		wifi_main();
		Delay(5000);
		strcpy(domain,"210.121.154.235");
		RxBufferTail = RxBufferHead;
		while(1)
		{
			Delay(50);
			/////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////
			
			switch(displayMode)
			{
				case '1' : // 재실
					
					img_DISPLAY("hw-01.bmp");
					status[0] = displayMode;
					test_change(domain, status, 0);
					displayModeBuffer = displayMode;
				
					while(1)
					{
						keyReturn = key_input_userdata();
						
						if(status[0] != displayMode) break; 
						if(keyReturn==1)
						{
							displayMode='9';
							
							keyReturn=0;
							break;
						}
					}break;
					
				case '2' : // 강의
					
					img_DISPLAY("hw-02.bmp");
					status[0] = displayMode;
					test_change(domain, status, 0);
					displayModeBuffer = displayMode;
				
					while(1)
					{
						keyReturn = key_input_userdata();
						
						if(status[0] != displayMode) break; 
						if(keyReturn==1)
						{
							displayMode='9';
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==2)
						{
							displayMode='6';
							printf("\r\n학생 %s\r\n",userData);
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==3)
						{
							displayMode='7';
							printf("\r\n누구쇼 %s\r\n",userData);
							keyReturn=0;
							break;
						}
					}break;
					
					case '3' : // 교내
					
					img_DISPLAY("hw-05.bmp");
					status[0] = displayMode;
					test_change(domain, status, 0);
					displayModeBuffer = displayMode;
					
					while(1)
					{
						keyReturn = key_input_userdata();
						
						if(status[0] != displayMode) break; 
						if(keyReturn==1)
						{
							displayMode='9';
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==2)
						{
							displayMode='6';
							printf("\r\n학생 %s\r\n",userData);
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==3)
						{
							displayMode='7';
							printf("\r\n누구쇼 %s\r\n",userData);
							keyReturn=0;
							break;
						}
					}break;
					
					case '4' : // 외출
					
					img_DISPLAY("hw-04.bmp");
					status[0] = displayMode;
					test_change(domain, status, 0);
					displayModeBuffer = displayMode;
					
					while(1)
					{
						keyReturn = key_input_userdata();
						
						if(status[0] != displayMode) break; 
						if(keyReturn==1)
						{
							displayMode='9';
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==2)
						{
							displayMode='6';
							printf("\r\n학생 %s\r\n",userData);
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==3)
						{
							displayMode='7';
							printf("\r\n누구쇼 %s\r\n",userData);
							keyReturn=0;
							break;
						}
					}break;
					
					case '5' : // 퇴교
					
					img_DISPLAY("hw-06.bmp");
					status[0] = displayMode;
					test_change(domain, status, 0);
					displayModeBuffer = displayMode;
					
					while(1)
					{
						keyReturn = key_input_userdata();
						
						if(status[0] != displayMode) break; 
						if(keyReturn==1)
						{
							displayMode='9';
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==2)
						{
							displayMode='6';
							printf("\r\n학생 %s\r\n",userData);
							keyReturn=0;
							break;
						}
						
						else if(keyReturn==3)
						{
							displayMode='7';
							printf("\r\n누구쇼 %s\r\n",userData);
							keyReturn=0;
							break;
						}
					}break;
					
					case '6' : // 학생 목적선택
					
					img_DISPLAY("hw-07.bmp");
				
					while(1)
					{
						keyReturn = key_input_menu();
						
						if(keyReturn == '*')
						{
							displayMode=displayModeBuffer;
							break;
						}
						else if(keyReturn>'0' && keyReturn<'5')
						{
							purpose[0] = keyReturn;
							displayMode = '8';
							break;
						}
					}

					break;
					
					case '7' : // 외부인 목적선택
					
					img_DISPLAY("hw-08.bmp");
				
					while(1)
					{
						keyReturn = key_input_menu();
						
						if(keyReturn == '*')
						{
							displayMode=displayModeBuffer;
							break;
						}
						else if(keyReturn>'0' && keyReturn<'5')
						{
							purpose[0] = keyReturn;
							displayMode = '8';
							break;
						}
					}
					break;
					
					case '8' : // 전송 완료
					
					//*******************************
					LCD_WriteRAM_Prepare();
					DMA_Cmd(DMA2_Stream1, ENABLE);
					DCMI_Cmd(ENABLE);
					DCMI_CaptureCmd(ENABLE);
					Delay(70);
					
					DMA_Cmd(DMA2_Stream1, DISABLE);
					
					Capture_Image_TO_Bmp();
					//**********************************/
					
					DCMI_Cmd(DISABLE);
					DCMI_CaptureCmd(DISABLE);
					
					img_DISPLAY("hw-09.bmp");
					//**********************************/
					test_domain(domain, purpose, userData, 0);
					//**********************************/
					Delay(1500);
					purpose[0] = 0;
					displayMode = displayModeBuffer;
					break;
					
					case '9' : // 상태 선택
					
					img_DISPLAY("hw-10.bmp");
					
					keyReturn = key_input_menu();
					if(keyReturn != '*')
						displayMode = keyReturn;
					else
						displayMode = displayModeBuffer;
					break;
			}
		}
	}
}


char key_input_menu(void)
{
	
	while(1)
	{
		curr_KeyPad_V = Key_Pad_Check();
		if (curr_KeyPad_V != prev_KeyPad_V)
		{
			prev_KeyPad_V = curr_KeyPad_V;
			if (curr_KeyPad_V) 
			{
				switch(curr_KeyPad_V)
				{
					case BTN_1:
						return '1';
					
					case BTN_2:
						return '2';
					
					case BTN_3:
						return '3';
					
					case BTN_4:
						return '4';
					
					case BTN_5:
						return '5';
					
					case BTN_X:
						return '*';
				}
			}
		}
	}
}

	
char key_input_userdata(void)
{
	int cnt=0;
	int i=0;
	
	for(i=0; i<15; i++)
	{
		userData[i] = NULL;
	}
	
	while(curr_KeyPad_V!=BTN_S)
	{
		curr_KeyPad_V = Key_Pad_Check();
		if(timerkey == 2000000)
		{
			if(check_status()==1) break;
			timerkey = 0;
		}
		timerkey++;
		if (curr_KeyPad_V != prev_KeyPad_V)
		{
			prev_KeyPad_V = curr_KeyPad_V;
			if (curr_KeyPad_V) 
			{
				switch(curr_KeyPad_V)
				{
					case BTN_0:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num0.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '0';
						}
						break;
					
					case BTN_1:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num1.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '1';
						}
						break;
					
					case BTN_2:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num2.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '2';
						}
						break;
					
					case BTN_3:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num3.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '3';
						}
						break;
					
					case BTN_4:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num4.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '4';
						}
						break;
					
					case BTN_5:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num5.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '5';
						}
						break;
					
					case BTN_6:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num6.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '6';
						}
						break;
					
					case BTN_7:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num7.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '7';
						}
						break;
					
					case BTN_8:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num8.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '8';
						}
						break;
					
					case BTN_9:
						if(cnt<11)
						{
							if(displayMode != '1')
							{
								num_DISPLAY("num9.bmp");
								pos_x+=18;
							}
							userData[cnt++] = '9';
						}
						break;
					
					case BTN_X:
						if(cnt>0)
						{
							if(displayMode != '1')
							{
								pos_x-=18;
								num_DISPLAY("blank.bmp");
							}
							userData[cnt-1] = NULL;
							cnt--;
							break;
						}
						else break;
					
					case BTN_S:
						cnt--;

						if(cnt==3 && userData[0]==password[0] && userData[1]==password[1] && userData[2]==password[2]  && userData[3]==password[3])
							keyReturn = 1;
						else if(cnt==7 && userData[7]!=NULL)
							keyReturn = 2;
						else if((cnt==9 && userData[9]!=NULL) || (cnt==10 && userData[10]!=NULL))
							keyReturn = 3;
						else
						{
							printf("다시 입력하세요\r\n");
							if(displayMode != '1')	num_CLEAR();
						}
						 break;
				}
			}
		}
	}curr_KeyPad_V=0; pos_x=60; return keyReturn;
}
	




// USART interrupt handler from wifi


/*
void USART3_IRQHandler(){  //sn8200_hal.c Interrupt handler
	  if(USART_GetITStatus(USART3, USART_IT_RXNE) != RESET) {
		recieveData=USART_ReceiveData(USART3);
		if(recieveData=='#' && usartRxInterruptStatus==0)
			usartRxInterruptStatus++;
		else if(recieveData=='$' && usartRxInterruptStatus==1)
			usartRxInterruptStatus++;
		else if(recieveData=='#' && usartRxInterruptStatus==2) // displaymode #$#
			usartRxInterruptStatus++;
		else if(recieveData=='@' && usartRxInterruptStatus==1)
			usartRxInterruptStatus=4;
		else if(recieveData=='#' && usartRxInterruptStatus==4)   // message #@#
			usartRxInterruptStatus=5;
		
		if(usartRxInterruptStatus==3){
			switch(recieveData){
				case '0' :
					displayMode='0';
					break;
				case '1' :
					displayMode='1';
					break;
				case '2' :
					displayMode='2';
					break;
				case '3' :
					displayMode='3';
					break;
				case '4':
					displayMode='4';
					break;
					case '5':
					displayMode='5';
					break;
			}
			usartRxInterruptStatus=0;
		}
		else if(usartRxInterruptStatus==5){
			message[uartRxCounter++]=recieveData;
			if(recieveData=='&' && endMsm==0) //&%& 메세지 끝
				endMsm++;
			else if(recieveData=='%' && endMsm==1)
				endMsm++;
			else if(recieveData=='&' && endMsm==2){
				uartRxCounter=0;
				usartRxInterruptStatus=0;
				endMsm=0;
			}
			else
				endMsm=0;
			
		}
		else
			usartRxInterruptStatus=0;
	}
}
*/







/**
  * @brief  Configures all needed resources (I2C, DCMI and DMA) to interface with
  *         the OV9655 camera module
  * @param  None
  * @retval 0x00 Camera module configured correctly 
  *         0xFF Camera module configuration failed
  */
uint8_t DCMI_OV9655Config(void)
{
  /* I2C1 will be used for OV9655 camera configuration */
  I2C1_Config();

  /* Reset and check the presence of the OV9655 camera module */
  if (DCMI_SingleRandomWrite(OV9655_DEVICE_WRITE_ADDRESS,0x12, 0x80))
  {
     return (0xFF);
  }

  /* OV9655 Camera size setup */    
  DCMI_OV9655_QVGASizeSetup();

  /* Set the RGB565 mode */
  DCMI_SingleRandomWrite(OV9655_DEVICE_WRITE_ADDRESS, OV9655_COM7, 0x63);
  DCMI_SingleRandomWrite(OV9655_DEVICE_WRITE_ADDRESS, OV9655_COM15, 0x10);

  /* Invert the HRef signal*/
  DCMI_SingleRandomWrite(OV9655_DEVICE_WRITE_ADDRESS, OV9655_COM10, 0x08);

  /* Configure the DCMI to interface with the OV9655 camera module */
  DCMI_Config();
  
  return (0x00);
}

/**
  * @brief  Configures the I2C1 used for OV9655 camera module configuration.
  * @param  None
  * @retval None
  */
void I2C1_Config(void)
{
  GPIO_InitTypeDef GPIO_InitStructure;
  I2C_InitTypeDef  I2C_InitStruct;

 /* I2C1 clock enable */
  RCC_APB1PeriphClockCmd(RCC_APB1Periph_I2C1, ENABLE);
  /* GPIOB clock enable */
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE); 

  /* Connect I2C1 pins to AF4 ************************************************/
  GPIO_PinAFConfig(GPIOB, GPIO_PinSource9, GPIO_AF_I2C1);
  GPIO_PinAFConfig(GPIOB, GPIO_PinSource8, GPIO_AF_I2C1);  
  
  /* Configure I2C1 GPIOs *****************************************************/  
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_8 | GPIO_Pin_9;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_2MHz;
  GPIO_InitStructure.GPIO_OType = GPIO_OType_OD;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;   
  GPIO_Init(GPIOB, &GPIO_InitStructure);

  /* Configure I2C1 ***********************************************************/  
  /* I2C DeInit */   
  I2C_DeInit(I2C1);
    
  /* Enable the I2C peripheral */
  I2C_Cmd(I2C1, ENABLE);
 
  /* Set the I2C structure parameters */
  I2C_InitStruct.I2C_Mode = I2C_Mode_I2C;
  I2C_InitStruct.I2C_DutyCycle = I2C_DutyCycle_2;
  I2C_InitStruct.I2C_OwnAddress1 = 0xFE;
  I2C_InitStruct.I2C_Ack = I2C_Ack_Enable;
  I2C_InitStruct.I2C_AcknowledgedAddress = I2C_AcknowledgedAddress_7bit;
  I2C_InitStruct.I2C_ClockSpeed = 30000;
  
  /* Initialize the I2C peripheral w/ selected parameters */
  I2C_Init(I2C1, &I2C_InitStruct);
}

/**
  * @brief  Configures the DCMI to interface with the OV9655 camera module.
  * @param  None
  * @retval None
  */
void DCMI_Config(void)
{
  DCMI_InitTypeDef DCMI_InitStructure;
  GPIO_InitTypeDef GPIO_InitStructure;
  DMA_InitTypeDef  DMA_InitStructure;
  
  /* Enable DCMI GPIOs clocks */
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC | RCC_AHB1Periph_GPIOE | 
                         RCC_AHB1Periph_GPIOB | RCC_AHB1Periph_GPIOA, ENABLE); 

  /* Enable DCMI clock */
  RCC_AHB2PeriphClockCmd(RCC_AHB2Periph_DCMI, ENABLE);

  /* Connect DCMI pins to AF13 ************************************************/
  /* PCLK */
  GPIO_PinAFConfig(GPIOA, GPIO_PinSource6, GPIO_AF_DCMI);
  /* D0-D7 */
  GPIO_PinAFConfig(GPIOC, GPIO_PinSource6, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOC, GPIO_PinSource7, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOE, GPIO_PinSource0, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOE, GPIO_PinSource1, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOE, GPIO_PinSource4, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOB, GPIO_PinSource6, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOE, GPIO_PinSource5, GPIO_AF_DCMI);
  GPIO_PinAFConfig(GPIOE, GPIO_PinSource6, GPIO_AF_DCMI);
  /* VSYNC */
  GPIO_PinAFConfig(GPIOB, GPIO_PinSource7, GPIO_AF_DCMI);
  /* HSYNC */
  GPIO_PinAFConfig(GPIOA, GPIO_PinSource4, GPIO_AF_DCMI);
  
  /* DCMI GPIO configuration **************************************************/
  /* D0 D1(PC6/7) */
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_6 | GPIO_Pin_7;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_100MHz;
  GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP ;  
  GPIO_Init(GPIOC, &GPIO_InitStructure);

  /* D2..D4(PE0/1/4) D6/D7(PE5/6) */
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0 | GPIO_Pin_1 
	                              | GPIO_Pin_4 | GPIO_Pin_5 | GPIO_Pin_6;
  GPIO_Init(GPIOE, &GPIO_InitStructure);

  /* D5(PB6), VSYNC(PB7) */
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_6 | GPIO_Pin_7;
  GPIO_Init(GPIOB, &GPIO_InitStructure);

  /* PCLK(PA6) HSYNC(PA4)*/
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_4 | GPIO_Pin_6;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF;
  GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
  GPIO_Init(GPIOA, &GPIO_InitStructure);
  
  /* DCMI configuration *******************************************************/ 
  DCMI_InitStructure.DCMI_CaptureMode = DCMI_CaptureMode_Continuous;
  DCMI_InitStructure.DCMI_SynchroMode = DCMI_SynchroMode_Hardware;
  DCMI_InitStructure.DCMI_PCKPolarity = DCMI_PCKPolarity_Falling;
  DCMI_InitStructure.DCMI_VSPolarity = DCMI_VSPolarity_High;
  DCMI_InitStructure.DCMI_HSPolarity = DCMI_HSPolarity_High;
  DCMI_InitStructure.DCMI_CaptureRate = DCMI_CaptureRate_All_Frame;
  DCMI_InitStructure.DCMI_ExtendedDataMode = DCMI_ExtendedDataMode_8b;
  
  DCMI_Init(&DCMI_InitStructure);

  /* Configures the DMA2 to transfer Data from DCMI to the LCD ****************/
  /* Enable DMA2 clock */
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_DMA2, ENABLE);  
  
  /* DMA2 Stream1 Configuration */  
  DMA_DeInit(DMA2_Stream1);

  DMA_InitStructure.DMA_Channel = DMA_Channel_1;  
  DMA_InitStructure.DMA_PeripheralBaseAddr = DCMI_DR_ADDRESS;	
  DMA_InitStructure.DMA_Memory0BaseAddr = FSMC_LCD_ADDRESS;
  DMA_InitStructure.DMA_DIR = DMA_DIR_PeripheralToMemory;
  DMA_InitStructure.DMA_BufferSize = 1;
  DMA_InitStructure.DMA_PeripheralInc = DMA_PeripheralInc_Disable;
  DMA_InitStructure.DMA_MemoryInc = DMA_MemoryInc_Disable;
  DMA_InitStructure.DMA_PeripheralDataSize = DMA_PeripheralDataSize_Word;
  DMA_InitStructure.DMA_MemoryDataSize = DMA_MemoryDataSize_HalfWord;
  DMA_InitStructure.DMA_Mode = DMA_Mode_Circular;
  DMA_InitStructure.DMA_Priority = DMA_Priority_High;
  DMA_InitStructure.DMA_FIFOMode = DMA_FIFOMode_Enable;         
  DMA_InitStructure.DMA_FIFOThreshold = DMA_FIFOThreshold_Full;
  DMA_InitStructure.DMA_MemoryBurst = DMA_MemoryBurst_Single;
  DMA_InitStructure.DMA_PeripheralBurst = DMA_PeripheralBurst_Single;
     
  DMA_Init(DMA2_Stream1, &DMA_InitStructure);
}

/**

  * @brief  
  * @param  None
  * @retval None
  */
void LIS302DL_Reset(void)
{
  uint8_t ctrl = 0;
  
  LIS302DL_InitTypeDef  LIS302DL_InitStruct;
  LIS302DL_InterruptConfigTypeDef LIS302DL_InterruptStruct;  
  
  /* Set configuration of LIS302DL*/
  LIS302DL_InitStruct.Power_Mode = LIS302DL_LOWPOWERMODE_ACTIVE;
  LIS302DL_InitStruct.Output_DataRate = LIS302DL_DATARATE_100;
  LIS302DL_InitStruct.Axes_Enable = LIS302DL_X_ENABLE | LIS302DL_Y_ENABLE | LIS302DL_Z_ENABLE;
  LIS302DL_InitStruct.Full_Scale = LIS302DL_FULLSCALE_2_3;
  LIS302DL_InitStruct.Self_Test = LIS302DL_SELFTEST_NORMAL;
  LIS302DL_Init(&LIS302DL_InitStruct);
    
  /* Set configuration of Internal High Pass Filter of LIS302DL*/
  LIS302DL_InterruptStruct.Latch_Request = LIS302DL_INTERRUPTREQUEST_LATCHED;
  LIS302DL_InterruptStruct.SingleClick_Axes = LIS302DL_CLICKINTERRUPT_Z_ENABLE;
  LIS302DL_InterruptStruct.DoubleClick_Axes = LIS302DL_DOUBLECLICKINTERRUPT_Z_ENABLE;
  LIS302DL_InterruptConfig(&LIS302DL_InterruptStruct);

  /* Required delay for the MEMS Accelerometre: Turn-on time = 3/Output data Rate 
                                                             = 3/100 = 30ms */
  Delay(30);
  
  /* Configure Click Window */
  ctrl = 0xC0;
  LIS302DL_Write(&ctrl, LIS302DL_CLICK_CTRL_REG3_ADDR, 1);
}

/**
  * @brief  MEMS accelerometre management of the timeout situation.
  * @param  None.
  * @retval None.
  */
uint32_t LIS302DL_TIMEOUT_UserCallback(void)
{
  /* MEMS Accelerometer Timeout error occured */
  while (1) ;
}
 
/**
  * @brief  Configures EXTI Line0 (connected to PA0 pin) in interrupt mode
  * @param  None
  * @retval None
  */
void EXTILine0_Config(void)
{
  
  GPIO_InitTypeDef   GPIO_InitStructure;
  NVIC_InitTypeDef   NVIC_InitStructure;

  /* Enable GPIOA clock */
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOA, ENABLE);
  /* Enable SYSCFG clock */
  RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);
  
  /* Configure PA0 pin as input floating */
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0;
  GPIO_Init(GPIOA, &GPIO_InitStructure);

  /* Connect EXTI Line0 to PA0 pin */
  SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOA, EXTI_PinSource0);

  /* Configure EXTI Line0 */
  EXTI_InitStructure.EXTI_Line = EXTI_Line0;
  EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;
  EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Rising_Falling;  
  EXTI_InitStructure.EXTI_LineCmd = ENABLE;
  EXTI_Init(&EXTI_InitStructure);

  /* Enable and set EXTI Line0 Interrupt to the lowest priority */
  NVIC_InitStructure.NVIC_IRQChannel = EXTI0_IRQn;
  NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0x01;
  NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0x01;
  NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
  NVIC_Init(&NVIC_InitStructure);
}


void Key_Pad_Config(void)
{
 
	GPIO_InitTypeDef   GPIO_InitStructure;

	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOA, ENABLE);
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC, ENABLE);
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);

	//											    BUTTN0		  BUTTN1			BUTTN2		  BUTTN10		BUTTN11
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_1 | GPIO_Pin_5 | GPIO_Pin_7 | GPIO_Pin_8 | GPIO_Pin_9;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_Init(GPIOA, &GPIO_InitStructure);
	
	//											    BUTTN5		  BUTTN6			BUTTN7		  BUTTN8
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0 | GPIO_Pin_1 | GPIO_Pin_2 | GPIO_Pin_14;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_Init(GPIOB, &GPIO_InitStructure);

	//											    BUTTN3		  BUTTN4
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_4 | GPIO_Pin_5;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_Init(GPIOC, &GPIO_InitStructure);

	//											    BUTTN9
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_11;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_Init(GPIOD, &GPIO_InitStructure);
}

#define	BUTTN00_r		0x0002
#define	BUTTN01_r		0x0020
#define	BUTTN02_r		0x0080
#define	BUTTN03_r		0x0010
#define	BUTTN04_r		0x0020
#define	BUTTN05_r		0x0001
#define	BUTTN06_r		0x0002
#define	BUTTN07_r		0x0004
#define	BUTTN08_r		0x4000
#define	BUTTN09_r		0x0800
#define	BUTTN10_r		0x0100
#define	BUTTN11_r		0x0200

unsigned short Key_Pad_Check(void)
{
	unsigned short ret = 0;
	if ((GPIOA->IDR & BUTTN00_r) == 0) ret |= 0x0001; // Button 0
	if ((GPIOA->IDR & BUTTN01_r) == 0) ret |= 0x0002; // Button 1
	if ((GPIOA->IDR & BUTTN02_r) == 0) ret |= 0x0004; // Button 2
	if ((GPIOC->IDR & BUTTN03_r) == 0) ret |= 0x0008; // Button 3
	if ((GPIOC->IDR & BUTTN04_r) == 0) ret |= 0x0010; // Button 4
	if ((GPIOB->IDR & BUTTN05_r) == 0) ret |= 0x0020; // Button 5
	if ((GPIOB->IDR & BUTTN06_r) == 0) ret |= 0x0040; // Button 6
	if ((GPIOB->IDR & BUTTN07_r) == 0) ret |= 0x0080; // Button 7
	if ((GPIOB->IDR & BUTTN08_r) == 0) ret |= 0x0100; // Button 8
	if ((GPIOD->IDR & BUTTN09_r) == 0) ret |= 0x0200; // Button 9
	if ((GPIOA->IDR & BUTTN10_r) == 0) ret |= 0x0400; // Button 10
	if ((GPIOA->IDR & BUTTN11_r) == 0) ret |= 0x0800; // Button 11
	
	return ret;
		
}





/**
  * @brief  Inserts a delay time.
  * @param  nTime: specifies the delay time length, in milliseconds
  * @retval None
  */
void Delay(uint32_t nTime)
{
  TimingDelay = nTime;

  while (TimingDelay != 0);

}

/**

  * @brief  Decrements the TimingDelay variable.
  * @param  None
  * @retval None
  */
void TimingDelay_Decrement(void)
{
  if (TimingDelay != 0x00)
  { 
    TimingDelay--;
  }
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *   where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t* file, uint32_t line)
{ 
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

  /* Infinite loop */
  while (1)
  {
  }
}
#endif

/**
  * @}
  */



/*********** Portions COPYRIGHT 2012 Embest Tech. Co., Ltd.*****END OF FILE****/
